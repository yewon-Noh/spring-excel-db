package spring.exceldb.excel.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import spring.exceldb.exhibition.model.ExhibitionDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * 디비 내용으로 엑셀 파일 생성
     * @param list
     * @return
     */
    public XSSFWorkbook newExcelExhibition(List<ExhibitionDTO> list) throws IOException {
        XSSFWorkbook  workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("전시회");

        // 시트 열 너비 설정
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);

        // 헤더 생성
        Cell headerCell = null;
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
        }

        // 행 생성
        Row bodyRow = null;
        Cell bodyCell = null;
        for (int i = 0; i < list.size(); i++) {
            ExhibitionDTO dto = list.get(i);

            bodyRow = sheet.createRow(i + 1);

            bodyCell = bodyRow.createCell(0);
            bodyCell.setCellValue(dto.getName());
            bodyCell = bodyRow.createCell(1);
            bodyCell.setCellValue(dto.getLocation());
            bodyCell = bodyRow.createCell(2);
            bodyCell.setCellValue(dto.getCategory());
            bodyCell = bodyRow.createCell(3);
            bodyCell.setCellValue(dto.getStart());
            bodyCell = bodyRow.createCell(4);
            bodyCell.setCellValue(dto.getEnd());
            bodyCell = bodyRow.createCell(5);
            bodyCell.setCellValue(dto.getExhibits());
            bodyCell = bodyRow.createCell(6);
            bodyCell.setCellValue(dto.getContent());
        }
        return workbook;
    }

    /**
     * 파일 이름 생성
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public String newExcelFileName(HttpServletRequest request, String name) throws UnsupportedEncodingException {
        Date date = new Date();
        String datetime = new SimpleDateFormat("yyyyMMddhhmmss").format(date);
        String fileName = name + "_" + datetime;

        // 브라우저에 따른 파일이름 인코딩작업
        String browser = request.getHeader("User-Agent");
        if (browser.indexOf("MSIE") > -1) {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.indexOf("Trident") > -1) {       // IE11
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser.indexOf("Firefox") > -1) {
            fileName = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.indexOf("Opera") > -1) {
            fileName = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1") + "\"";
        } else if (browser.indexOf("Chrome") > -1) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fileName.length(); i++) {
                char c = fileName.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            fileName = sb.toString();
        } else if (browser.indexOf("Safari") > -1){
            fileName = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1")+ "\"";
        } else {
            fileName = "\"" + new String(fileName.getBytes("UTF-8"), "8859_1")+ "\"";
        }

        return fileName;
    }

    /**
     * 엑셀 파일 다운로드
     * @param request
     * @param response
     * @param list
     * @throws IOException
     */
    public void downloadExcel(HttpServletRequest request, HttpServletResponse response, List<ExhibitionDTO> list) {
        try {
            // 엑셀 파일 생성
            XSSFWorkbook workbook = newExcelExhibition(list);

            // Header, ContentType 설정
            response.setHeader("Set-Cookie", "fileDownload=true; path=/");
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=\"%s\"", newExcelFileName(request, "전시회") + ".xlsx", "UTF-8"));
            response.setContentType("application/vnd.ms-excel");

            workbook.write(response.getOutputStream());
            response.getOutputStream().close();
            ((XSSFWorkbook) workbook).close();

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
