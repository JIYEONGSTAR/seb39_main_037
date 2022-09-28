//package com.main.project.S3;
//
//import com.amazonaws.services.s3.model.CannedAccessControlList;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.amazonaws.services.s3.model.S3Object;
//import com.amazonaws.services.s3.model.S3ObjectInputStream;
//import com.amazonaws.util.IOUtils;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import com.amazonaws.services.s3.AmazonS3Client;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class S3Service {
//
//
//    @Autowired
//    private final AmazonS3Client amazonS3Client;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucketName;
//
//
//    /* S3 버킷의 reviewimg 디렉토리에 이미지를 저장한다. */
//    public String uploadMutipartFile(MultipartFile multipartFile, String dirName) throws IOException {
//            File file = convertMultiPartFileToFile(multipartFile).orElseThrow(() -> new IllegalArgumentException("멀티파트파일을 파일로 전환하는데 실패 "));//사진파일을 받아 s3넣기 전에 file로 변환
//
//        return  upload(file, dirName, file.getName());
//    }
//
//
//    /* S3 버킷의 reviewimg 디렉토리에 이미지를 저장한다. */
//    public String upload(File uploadFile, String filePath, String saveFileName) {
//        String path = filePath + "/" + saveFileName;
//        amazonS3Client.putObject(new PutObjectRequest(bucketName, path, uploadFile)
//                .withCannedAcl(CannedAccessControlList.PublicRead)); // public 권한으로 설정
//
//        return amazonS3Client.getUrl(bucketName, path).toString();
//    }
//
//    /* S3 버킷의 reviewimg 디렉토리에 있는 이미지 파일을 가져올 수 있도록 url을 제공.*/
//    public String findeImgUrl(String filename){
//        String path = "reviewimg" + "/" + filename;
//        return amazonS3Client.getUrl(bucketName, path).toString();
//    }
//
//
//
////    public byte[] download(String fileName){
////
////        S3Object  object = amazonS3Client.getObject(bucketName, fileName);
////        S3ObjectInputStream objectContent = object.getObjectContent();
////        try{
////            return IOUtils.toByteArray(objectContent);
////        }catch (IOException e){
////            throw   new RuntimeException(e);
////        }
////
////    }
//
//    /* S3 버킷의 reviewimg 디렉토리에 있는 이미지 파일을 지운다.*/
//    public String deleteFile(String fileName){
//        amazonS3Client.deleteObject(bucketName, fileName);
//        return "filedeleted";
//
//    }
//
//    /* request에서 이미지파일을 받아와 File 타입으로 변환하는 메서드 */
//    public Optional<File> convertMultiPartFileToFile (MultipartFile file) throws IOException{
//
//        File convFile = new File(file.getOriginalFilename());
//        System.out.println("convertFile = " + convFile);
//
//
//            FileOutputStream fos = new FileOutputStream(convFile);
//            fos.write(file.getBytes());
//
//            return Optional.of(convFile);
//
//    }
//
//
//
//}