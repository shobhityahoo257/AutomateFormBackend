package com.formsv.AutomateForm.controller;


import com.formsv.AutomateForm.Constants.Constants;
import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.AppliedForm;
import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.transaction.UserInteraction;
import com.formsv.AutomateForm.model.user.User;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.responseModel.FamilyResponse;

import com.formsv.AutomateForm.security.AuthenticationRequest;
import com.formsv.AutomateForm.security.JwtFilters;
import com.formsv.AutomateForm.security.util.JwtUtil;
import com.formsv.AutomateForm.service.SupportedDocService;
import com.formsv.AutomateForm.service.SupportedFieldsService;
import com.formsv.AutomateForm.service.UserInteractionService;
import com.formsv.AutomateForm.service.form.AppliedFormService;
import com.formsv.AutomateForm.service.form.FormService;
import com.formsv.AutomateForm.service.image.ImageService;
import com.formsv.AutomateForm.service.user.UserDataService;
import com.formsv.AutomateForm.service.user.UserDocumentService;
import com.formsv.AutomateForm.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController()
public class AppSideController {

    private static final Logger log = LoggerFactory.getLogger(AppSideController.class);


    @Autowired
    UserService userService;
    @Autowired
    SupportedFieldsService supportedFieldsService;
    @Autowired
    SupportedDocService supportedDocService;
    @Autowired
    UserDataService userDataService;
    @Autowired
    AppliedFormService appliedFormService;
    @Autowired
    ImageService imageService;
    @Autowired
    FormService formService;

    @Autowired
    JwtUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserInteractionService userInteractionService;

    @Autowired
    UserDocumentService userDocumentService;



    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        log.info("Authentication Started for :"+authenticationRequest.toString());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
      log.info("Authenticated for Generating Token");
        log.info("Generating Token and Loading user");
 //Password =userId
        final UserDetails userDetails = userService
                .loadUserByUsername(authenticationRequest.getUsername());
        if(userDetails!=null)
        log.info("User is Loaded");
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        log.info("Token Is created Returning Token");
        return ResponseEntity.ok(jwt);
    }





    @GetMapping("getFamily/{mobileNumber}")
    public ResponseEntity getFamily(@PathVariable("mobileNumber") String mobileNumber) throws Exception {
        UserInteraction ui=new UserInteraction();
        ResponseEntity res;
        ui.setEndPoint("getFamily/"+mobileNumber);
        ui=userInteractionService.saveInteraction(ui);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(Constants.TRANSACTIONID.value, ui.get_id());
       if( !userService.isFamilyExist(mobileNumber) )
       {
         res=new ResponseEntity(ExceptionConstants.NOFAMILY.value,headers,HttpStatus.BAD_REQUEST);
          ui.setResponse(res);
       }
       else {
           FamilyResponse fr = userService.getFamily(mobileNumber);
           res=new ResponseEntity(fr,headers,HttpStatus.OK);
           for (int i=0;i<fr.getUsers().size();i++) {

           }
           ui.setResponse(fr);
       }
       userInteractionService.saveInteraction(ui);
       return res;
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestParam("userName") String userName,@RequestParam("mobileNumber") String mobileNumber,@RequestParam(value = "profileImage",required = false) MultipartFile profileImage ) throws Exception {
        if(userService.isFamilyExist(mobileNumber))
            return new ResponseEntity(ExceptionConstants.DATAALREADYEXIST.value,HttpStatus.BAD_REQUEST);
        User user=new User();
        user.setParent(true);
        user.setUserName(userName);
        user.setMobileNumber(mobileNumber);
        if(profileImage!=null)
        user.setProfileImageId(imageService.addImage(profileImage));
        user.setCreatedAt(new Date());
        user.setModifiedAt(user.getCreatedAt());

        return userService.createUser(user);
    }

    @PostMapping("/addNewMember/{userId}")
    public ResponseEntity<String> addMember(@PathVariable("userId") String userId,@RequestParam("userName") String userName,@RequestParam(value = "profileImage",required = false) MultipartFile profileImage ) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED.value, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        }
        User existingParent=userService.findById(userId);
//        if( !existingParent.getMobileNumber().equals(mobileNumber))
//            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        //Setting Lock
        userService.setLock(userId);
        User user=new User();
        user.setUserName(userName);
        user.setMobileNumber(existingParent.getMobileNumber());
        user.setCreatedAt(new Date());
        user.setModifiedAt(user.getCreatedAt());
        if(profileImage!=null)
        user.setProfileImageId(imageService.addImage(profileImage));
        ResponseEntity r=null;
        try {
            r = userService.addNewMember(user);
        }catch (Exception e){
            throw e;
        }finally {
            userService.releaseLock(userId);
        }
        return r;
    }

    @PutMapping("/updateUser/{userId}/{userName}")
    public ResponseEntity updateUser(@PathVariable("userId") String userId,@RequestParam(value = "profileImage",required = false) MultipartFile profileImage ,@PathVariable("userName") String userName) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED.value, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        }
              User u=null;
               userService.setLock(userId);
          try {
              u = userService.updateUser(userId, profileImage, userName);
          }catch (Exception e)
          {
              throw e;
          }
          finally {
              userService.releaseLock(userId);
          }
               return new ResponseEntity(u,HttpStatus.OK);
    }

    /**
     * MobileNumber Can be updated By userId of any member of the family
     * @param userId
     * @param mobileNumber
     * @return
     */
    @PutMapping("/updateMobileNumber/{userId}/{mobileNumber}")
    public ResponseEntity updateMobileNumber(@PathVariable("userId") String userId,@PathVariable("mobileNumber") String mobileNumber) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED.value, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        }
        List<User> u=null;
             userService.setLock(userId);
        try {
            u = userService.updateMobileNumber(userId, mobileNumber);
        }catch (Exception e) {
            throw  e;
        }finally {
            userService.releaseLock(userId);
        }
            return new ResponseEntity(u,HttpStatus.OK);
    }


    @GetMapping("/getAllForms/{userId}")
    public ResponseEntity getAllFormsOfUser(@PathVariable("userId") String userId) throws Exception {
        if(!userService.isUserExistById(userId))
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        return formService.getAllFormsOfUser(userId);
    }

    @PostMapping("/submitForm/{userId}/{formId}")
    public ResponseEntity applyForm(@PathVariable("formId") String formid,@PathVariable("userId") String userId) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED.value, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        }
        userService.setLock(userId);
        AppliedForm appliedForm = new AppliedForm();
        appliedForm.setFormId(formid);
        appliedForm.setUserId(userId);
        ResponseEntity r=appliedFormService.create(appliedForm);
        userService.releaseLock(userId);
        return r;
    }


    /*
      This is used to upload single document of user
     */

    @PostMapping("/uploadSingleImage/{userId}/{documentId}")
    public ResponseEntity addDocuments(@PathVariable("userId") String userId,
                                       @RequestParam(value = "documentFront") MultipartFile documentFront,@RequestParam(value = "documentBack") MultipartFile documentBack,@PathVariable("documentId") String documentId)
            throws Exception {
       if(documentFront==null || documentFront.isEmpty() || documentFront==null || documentFront.isEmpty())
           return new ResponseEntity(ExceptionConstants.NODOCUMENT.value,HttpStatus.BAD_REQUEST);
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED.value, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        }
        ResponseEntity r=null;
        userService.setLock(userId);
        try {
             r = userDocumentService.addDocument(userId, documentId, documentFront,documentBack);
        }catch (Exception e)
        {
            throw e;
        }finally {
            userService.releaseLock(userId);
        }
        return r;
    }

    @PutMapping("updateDocuments/{userId}/{documentId}")
    public ResponseEntity updateDocument(@PathVariable("userId") String userId,@RequestParam("document") MultipartFile document,@PathVariable("documentId") String documentId) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED.value, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        }
        UserDocuments d=null;
          userService.setLock(userId);
        try {
             d = userDocumentService.updateUserDocument(userId, documentId, document);
        }catch (Exception e){
            throw e;
        }finally {
            userService.releaseLock(userId);
        }

           return new ResponseEntity(d,HttpStatus.OK);

    }

    /**
     * Get Required Documents For a form
     * @param userId
     * @param formId
     * @return
     */
    @GetMapping("/getRequiredDocument/{userId}/{formId}")
    public ResponseEntity getRequiredDocument(@PathVariable("userId") String userId,@PathVariable("formId") String formId){
        if(!userService.isUserExistById(userId))
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        return userService.getRequiredDocument(userId,formId);
    }


    /**
     * Get All Uploaded Documents Of user
     * @param userId
     * @return
     * @throws IOException
     */
    @GetMapping("/getAllDocumentsOfUser/{userId}")
    public  ResponseEntity getAllDocumentsOfUser(@PathVariable("userId") String userId) throws IOException {
         if(!userService.isUserExistById(userId))
             return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
         return new ResponseEntity(userDocumentService.getAllUserDocuments(userId), HttpStatus.OK);
    }

    /**
     * Get All Supported Documents Of System which can be uploaded by User Used In DropDown
     * @return
     */
    @GetMapping("/getAllSupportedDocuments/{userId}")
    public ResponseEntity getAllSupportedDocuments(@PathVariable("userId") String userId){
       return new ResponseEntity(supportedDocService.getAllSupportedDocuments(userId),HttpStatus.OK);
    }

    @DeleteMapping("/deleteDocument/{userId}/{documentId}")
    public ResponseEntity deleteDocument(@PathVariable("userId") String userId,@PathVariable("documentId") String documentId) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED.value, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        }
           userService.setLock(userId);
        try {
            userDocumentService.deleteDocumentById(documentId);
        }catch (Exception e)
        {
            throw e;
        }finally {
            userService.releaseLock(userId);
        }
           return new ResponseEntity(Constants.DELETED, HttpStatus.OK);
    }

    @GetMapping("/photos/{userid}/{id}")
    public ResponseEntity getImage(@PathVariable("userid") String userid,@PathVariable("id") String id) {
       if(!userService.isUserExistById(userid))
           return new ResponseEntity(ExceptionConstants.USERNOTFOUND.value,HttpStatus.BAD_REQUEST);
        Image photo = imageService.getImage(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photo.getImage());
    }

}




@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyDetailsService myUserDetailsService;
    @Autowired
    private JwtFilters jwtRequestFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }


    //userName =userId ,

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests().antMatchers("/authenticate",
                        "/getFamily/**",
                        "/createUser/**/**",
                        "/photos/**/**",
                "/deleteDocument/**/**",
                "/getAllSupportedDocuments/**",
                "/getAllDocumentsOfUser/**",
                        "/getRequiredDocument/**/**",
                "updateDocuments/**/**",
                "/uploadSingleImage/**/**",
                "/submitForm/**/**",
                "/getAllForms/**",
                "/updateMobileNumber/**/**",
                "/updateUser/**/**",
                "/addNewMember/**/**/**",



   //   Employee Side APIS
               "/getAllSupportedDoc",
                "/createForm",
                "/updateForm/**",
                "/addRequiredDocuments/**",
                "/deleteRequiredDocuments/**",
                "/getAllUsers",
                "/createSupportedDoc",
                "/getSupportedFields/**",
                "/getUserData/user/**/document/**",
                "/storeUserData/**",
                "/updateRequiredDocument/**",
                "/getFormDetails/**"
                ).permitAll().
                anyRequest().authenticated().and().
                exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }
}


