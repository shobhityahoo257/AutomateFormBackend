package com.formsv.AutomateForm.controller;


import com.formsv.AutomateForm.Constants.Constants;
import com.formsv.AutomateForm.Constants.ExceptionConstants;
import com.formsv.AutomateForm.model.form.AppliedForm;
import com.formsv.AutomateForm.model.image.Image;
import com.formsv.AutomateForm.model.user.User;
import com.formsv.AutomateForm.model.user.UserDocuments;
import com.formsv.AutomateForm.security.AuthenticationRequest;
import com.formsv.AutomateForm.security.JwtFilters;
import com.formsv.AutomateForm.security.util.JwtUtil;
import com.formsv.AutomateForm.service.SupportedDocService;
import com.formsv.AutomateForm.service.SupportedFieldsService;
import com.formsv.AutomateForm.service.form.AppliedFormService;
import com.formsv.AutomateForm.service.form.FormService;
import com.formsv.AutomateForm.service.image.ImageService;
import com.formsv.AutomateForm.service.user.UserDataService;
import com.formsv.AutomateForm.service.user.UserService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController()
public class AppSideController {

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



    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

 //Password =userId
        final UserDetails userDetails = userService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(jwt);
    }





    @GetMapping("getFamily/{mobileNumber}")
    public ResponseEntity getFamily(@PathVariable("mobileNumber") String mobileNumber) throws Exception {
        return userService.getFamily(mobileNumber);
    }

    @PostMapping("/createUser/{userName}/{mobileNumber}")
    public ResponseEntity<String> createUser(@PathVariable("userName") String userName,@PathVariable("mobileNumber") String mobileNumber,@RequestParam(value = "profileImage",required = false) MultipartFile profileImage ) throws Exception {
        User user=new User();
        user.setParent(true);
        user.setUserName(userName);
        user.setMobileNumber(mobileNumber);
        if(!(profileImage==null))
        user.setProfileImage(profileImage.getBytes());
        user.setCreatedAt(new Date());
        user.setModifiedAt(user.getCreatedAt());
        return userService.createUser(user);
    }

    @PostMapping("/addNewMember/{userId}/{userName}/{mobileNumber}")
    public ResponseEntity<String> addMember(@PathVariable("userId") String userId,@PathVariable("userName") String userName,@PathVariable("mobileNumber") String mobileNumber,@RequestParam("profileImage") MultipartFile profileImage ) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        }
        //Setting Lock
        userService.setLock(userId);
        User user=new User();
        user.setUserName(userName);
        user.setMobileNumber(mobileNumber);
        //user.setProfileImage(new Binary(BsonBinarySubType.BINARY, profileImage.getBytes()));
        user.setProfileImage(profileImage.getBytes());
        ResponseEntity r=userService.addNewMember(user);
        userService.releaseLock(userId);
        return r;
    }

    @PutMapping("/updateUser/{userId}/{userName}")
    public ResponseEntity updateUser(@PathVariable("userId") String userId,@RequestParam(value = "profileImage",required = false) MultipartFile profileImage ,@PathVariable("userName") String userName) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        }

               userService.setLock(userId);
               User u=userService.updateUser(userId, profileImage, userName);
               userService.releaseLock(userId);
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
                return new ResponseEntity(ExceptionConstants.USERLOCKED, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        }
             userService.setLock(userId);
             List<User> u=userService.updateMobileNumber(userId,mobileNumber);
             userService.releaseLock(userId);
            return new ResponseEntity(u,HttpStatus.OK);
    }


    @GetMapping("/getAllForms/{userId}")
    public ResponseEntity getAllFormsOfUser(@PathVariable("userId") String useId) throws Exception {
        return formService.getAllFormsOfUser(useId);
    }

    @PostMapping("/submitForm/{userId}/{formId}")
    public ResponseEntity applyForm(@PathVariable("formId") String formid,@PathVariable("userId") String userId) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
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
                                       @RequestParam("document") MultipartFile document,@PathVariable("documentId") String documentId)
            throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        }
        userService.setLock(userId);
        ResponseEntity r= imageService.addPhoto(userId,documentId,document);
        userService.releaseLock(userId);
        return r;
    }

    @PutMapping("updateDocuments/{userId}/{documentId}")
    public ResponseEntity updateDocument(@PathVariable("userId") String userId,@RequestParam("document") MultipartFile document,@PathVariable("documentId") String documentId) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        }
          userService.setLock(userId);
           UserDocuments d=userDataService.updateUserDocument(userId,documentId,document);
           userService.releaseLock(userId);
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
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
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
             return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
         return new ResponseEntity(userDataService.getAllUserDocuments(userId), HttpStatus.OK);
    }

    /**
     * Get All Supported Documents Of System
     * @return
     */
    @GetMapping("/getAllSupportedDocuments")
    public ResponseEntity getAllSupportedDocuments(){
       return new ResponseEntity(supportedDocService.getAllSupportedDocuments(),HttpStatus.OK);
    }

    @DeleteMapping("/deleteDocument/{userId}/{documentId}")
    public ResponseEntity deleteDocument(@PathVariable("userId") String userId,@PathVariable("documentId") String documentId) throws Exception {
        try {
            if (userService.isUserLocked(userId))
                return new ResponseEntity(ExceptionConstants.USERLOCKED, HttpStatus.CONFLICT);
        }catch (NullPointerException e){
            return new ResponseEntity(ExceptionConstants.USERNOTFOUND,HttpStatus.BAD_REQUEST);
        }
           userService.setLock(userId);
           userDataService.deleteDocumentById(documentId);
           userService.releaseLock(userId);
           return new ResponseEntity(Constants.DELETED, HttpStatus.OK);

    }








    @GetMapping("/photos/{id}")
    public ResponseEntity getPhoto(@PathVariable String id) {
        Image photo = imageService.getPhoto(id);
//        model.addAttribute("title", photo.getTitle());
//        model.addAttribute("image",
//                Base64.getEncoder().encodeToString(photo.getImage().getData()));
        // return new ResponseEntity(,HttpStatus.OK);// new ResponseEntity;
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photo.getImage().getData());
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
                .authorizeRequests().antMatchers("/authenticate","/getFamily/**","/createUser/**/**").permitAll().
                anyRequest().authenticated().and().
                exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }
}


