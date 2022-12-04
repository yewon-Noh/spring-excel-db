package spring.exceldb.excel.service;

import org.springframework.web.multipart.MultipartFile;
import spring.exceldb.api.ApiResponse;


public interface ExcelService {
    public ApiResponse<Object> uploadExcel(MultipartFile file);
}
