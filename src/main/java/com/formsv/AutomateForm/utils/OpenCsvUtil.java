package com.formsv.AutomateForm.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.formsv.AutomateForm.model.user.UserData;
import com.formsv.AutomateForm.service.user.UserDataService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.web.multipart.MultipartFile;


public class OpenCsvUtil {
    private static String csvExtension = "csv";
    private static String[] CSV_HEADER = { "id", "userId", "mobileNumber","userName"};



    public static Map<String,List<String>> parseCsvFile(String userId,InputStream is) throws Exception {
    // UserDataService userDataService=new UserDataService(userDataRepo, userService, imageService);
        List<String > fieldName=new ArrayList<>();
        List<String> fieldValue=new ArrayList<>();
        Map<String,List<String>> map=new HashMap<>();

        List<UserData> userDataList=new ArrayList<>();


        Reader fileReader = null;
        CsvToBean<UserData> csvToBean = null;

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(is));
            int i=0;
            String[] nextLine;
            //reads one line at a time
            while ((nextLine = reader.readNext()) != null) {
                for (String token : nextLine) {
                    if(i==0)
                        fieldName.add(token);
                    else
                        fieldValue.add(token);
                    System.out.print(token);
                }
                i++;
                System.out.print("\n");
            }
        } catch (Exception e)
       {e.printStackTrace();}

    //  userDataService.uploadUserdata(userId,fieldName,fieldValue);

        map.put("field",fieldName);
        map.put("fieldValue",fieldValue);
        return map;


//
//    List<UserData> userDatas = new ArrayList<UserData>();
//
//        try {
//            fileReader = new InputStreamReader(is);
//
//            ColumnPositionMappingStrategy<UserData> mappingStrategy = new ColumnPositionMappingStrategy<UserData>();
//
//            mappingStrategy.setType(UserData.class);
//            mappingStrategy.setColumnMapping(CSV_HEADER);
//
//            csvToBean = new CsvToBeanBuilder<UserData>(fileReader).withMappingStrategy(mappingStrategy).withSkipLines(1)
//                    .withIgnoreLeadingWhiteSpace(true).build();
//
//            userDatas = csvToBean.parse();
//
//            return userDatas;
//        } catch (Exception e) {
//            System.out.println("Reading CSV Error!");
//            e.printStackTrace();
//        } finally {
//            try {
//                fileReader.close();
//            } catch (IOException e) {
//                System.out.println("Closing fileReader/csvParser Error!");
//                e.printStackTrace();
//            }
//        }
//        return userDatas;
    }

    public static void UserDataToCsv(Writer writer, List<UserData> UserDatas) throws IOException {



        StatefulBeanToCsv<UserData> beanToCsv = null;

        try {
            // write List of Objects
            ColumnPositionMappingStrategy<UserData> mappingStrategy =
                    new ColumnPositionMappingStrategy<UserData>();

            mappingStrategy.setType(UserData.class);
            mappingStrategy.setColumnMapping(CSV_HEADER);

            beanToCsv = new StatefulBeanToCsvBuilder<UserData>(writer)
                    .withMappingStrategy(mappingStrategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();

            beanToCsv.write(UserDatas);
        } catch (Exception e) {
            System.out.println("Writing CSV error!");
            e.printStackTrace();
        }
    }

    public static boolean isCSVFile(MultipartFile file) {
        String extension = file.getOriginalFilename().split("\\.")[1];

        if(!extension.equals(csvExtension)) {
            return false;
        }

        return true;
    }
}