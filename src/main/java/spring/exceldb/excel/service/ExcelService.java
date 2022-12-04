package spring.exceldb.excel.service;

import org.springframework.web.multipart.MultipartFile;
import spring.exceldb.api.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface ExcelService {
    public ApiResponse<Object> uploadExcel(MultipartFile file);
    public void downloadExcel(HttpServletRequest request, HttpServletResponse response);
}
