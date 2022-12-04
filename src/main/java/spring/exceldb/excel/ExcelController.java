package spring.exceldb.excel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spring.exceldb.api.ApiResponse;
import spring.exceldb.excel.service.ExcelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class ExcelController {
    private final ExcelService excelService;

    @Autowired
    ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    /**
     * 엑셀을 디비에 업로드
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<? extends ApiResponse> uploadExcel(MultipartFile file) {
        ApiResponse apiResponse = excelService.uploadExcel(file);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /**
     * 디비 내용을 엑셀 파일로 다운로드
     * @param response
     * @param request
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) {
        excelService.downloadExcel(request, response);
    }
}
