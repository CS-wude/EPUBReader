# EPUBReader
基于java的EPUB阅读器   
前端使用vue3   
后端基于SpringBoot   
java版本1.8   
maven版本3.6.1   
node版本v20.19.3    


进入vue3文件夹，进行如下指令    
npm run install    
npm run serve     
进入EPUBReader后端文件夹，运行Main     

文件路径修改       
EpubController.java的      
 private final Path uploadDir = Paths.get("文件路径，比如G:\\book").toAbsolutePath();       
 
 epub文件夹中我上传了一个文件用以测试