package org.kosa.shoppingmaillmanager.host.product;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    // 실제 저장 경로 (운영에서는 외부 경로로 분리 권장)
	private final String uploadDir = "/opt/data/upload/product/main/";

	public String store(MultipartFile file) throws IOException {
	    File dir = new File(uploadDir);
	    if (!dir.exists()) {
	        dir.mkdirs();
	    }

	    String originalFilename = file.getOriginalFilename();
	    String extension = "";
	    if (originalFilename != null && originalFilename.contains(".")) {
	        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
	    }

	    String uuid = UUID.randomUUID().toString();
	    String newFileName = uuid + extension;

	    File destination = new File(uploadDir + newFileName);
	    file.transferTo(destination);

	    // ✅ 클라이언트에 반환할 URL 또는 상대경로
	    return "/upload/product/main/" + newFileName;
	}

}

