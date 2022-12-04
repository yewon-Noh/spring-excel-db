package spring.exceldb.excel.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ExcelUtil {
    // 전시회 헤더
    String[] headers = {"전시명", "전시 장소", "전시 구분", "전시 시작일", "전시 종료일", "전시품", "전시 요약"};

    /**
     * 엑셀 양식 확인
     * @param file
     * @param columnLength
     * @return
     */
    public boolean checkExcelHeader(MultipartFile file, int columnLength) {
        try {
            OPCPackage opcPackage = OPCPackage.open(file.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFRow row = sheet.getRow(1);

            for (int columnIndex = 0; columnIndex <= columnLength-1; columnIndex++) {
                XSSFCell cell = row.getCell(columnIndex);
                log.info(" 열 = " + getCellValue(cell));
                if(!getCellValue(cell).equals(headers[columnIndex]))
                    return false;
            }

        } catch (IOException | InvalidFormatException ex) {
            log.error(ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 셀의 데이터 타입에 맞게 값을 반환
     * @param cell
     * @return
     */
    public String getCellValue(XSSFCell cell) {
        String value = "";

        if (cell == null) return value;

        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                    value = new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                else
                    value = (int) cell.getNumericCellValue() + "";
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 엑셀 파일의 데이터 목록 가져옴
     * @param file
     * @param startRowNum 시트 시작 행
     * @param columnLength 시트 열의 총 개수
     * @return
     */
    public List<Map<String, Object>> getListData(MultipartFile file, int startRowNum, int columnLength) {
        List<Map<String, Object>> excelList = new ArrayList<Map<String,Object>>();

        try {
            OPCPackage opcPackage = OPCPackage.open(file.getInputStream());

            @SuppressWarnings("resource")
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);

            XSSFSheet sheet = workbook.getSheetAt(0);

            int rowIndex = 0;
            int columnIndex = 0;

            for (rowIndex = startRowNum; rowIndex < sheet.getLastRowNum() + 1; rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);

                if (row.getCell(0) != null && !row.getCell(0).toString().isBlank()) {

                    Map<String, Object> map = new HashMap<String, Object>();

                    int cells = columnLength-1;

                    for (columnIndex = 0; columnIndex <= cells; columnIndex++) {
                        XSSFCell cell = row.getCell(columnIndex);
                        map.put(String.valueOf(columnIndex), getCellValue(cell));
                        log.info(rowIndex + " 행 : " + columnIndex+ " 열 = " + getCellValue(cell));
                    }

                    excelList.add(map);
                }
            }

        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }

        return excelList;
    }
}
