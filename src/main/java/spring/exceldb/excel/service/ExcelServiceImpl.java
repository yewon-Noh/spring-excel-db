package spring.exceldb.excel.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring.exceldb.api.ApiResponse;
import spring.exceldb.api.Message;
import spring.exceldb.excel.utils.ExcelUtil;
import spring.exceldb.exhibition.model.Exhibition;
import spring.exceldb.exhibition.model.ExhibitionDTO;
import spring.exceldb.exhibition.repository.ExhibitionRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExcelServiceImpl implements ExcelService {

    private final ExhibitionRepository repository;
    private final ExcelUtil excelUtil;

    @Autowired
    ExcelServiceImpl(ExhibitionRepository repository, ExcelUtil excelUtil) {
        this.repository = repository;
        this.excelUtil = excelUtil;
    }

    /**
     * 엑셀을 디비에 업로드
     * @param file
     * @return
     */
    @Override
    public ApiResponse uploadExcel(MultipartFile file) {
        // 빈 파일인지 확인
        if (file.isEmpty()) {
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(Message.NO_FILES.label())
                    .build();
        }

        // 확장자가 xlsx 또는 xls 인지 확인
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        System.out.println("extension = " + extension);
        if (!(extension.toLowerCase().equals("xlsx"))) {
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(Message.BAD_EXCEL_TYPE.label())
                    .build();
        }

        // 엑셀 형식 확인
        if(!excelUtil.checkExcelHeader(file, 7)) {
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(Message.BAD_EXCEL_HEADERS.label())
                    .build();
        }

        // 엑셀 내용을 DTO에 저장
        List<Map<String, Object>> listMap = excelUtil.getListData(file, 2, 7);
        List<ExhibitionDTO> exhibitionDTOList = new ArrayList<>();
        for (Map<String, Object> map : listMap) {
            ExhibitionDTO dto = ExhibitionDTO.builder()
                    .name(map.get("0").toString())
                    .location(map.get("1").toString())
                    .category(map.get("2").toString())
                    .start(map.get("3").toString())
                    .end(map.get("4").toString())
                    .exhibits(map.get("5").toString())
                    .content(map.get("6").toString())
                    .build();

            exhibitionDTOList.add(dto);
        }

        // DB에 저장
        try {
            for (ExhibitionDTO dto: exhibitionDTOList) {
                repository.save(new Exhibition(dto));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message(Message.UPLOAD_EXCEL.label())
                .data(exhibitionDTOList)
                .build();
    }

    /**
     * 디비 내용을 엑셀 파일로 다운로드
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void downloadExcel(HttpServletRequest request, HttpServletResponse response){
        List<ExhibitionDTO> list = new ArrayList<>();
        for (Exhibition exhibition : repository.findAll()) {
            list.add(exhibition.setExcelDTO());
        }
        excelUtil.downloadExcel(request, response, list);
    }
}
