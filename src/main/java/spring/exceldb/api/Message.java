package spring.exceldb.api;

public enum Message {
    UPLOAD_EXCEL("업로드 되었습니다."),
    ERROR_UPLOAD_EXCEL("업로드 실패하였습니다."),
    NO_FILES("파일이 존재하지 않습니다."),
    BAD_EXCEL_TYPE("엑셀 파일(*.xlsx)만 업로드 가능합니다."),
    BAD_EXCEL_HEADERS("엑셀 양식이 올바르지 않습니다."),
    MAX_UPLOAD_SIZE("파일 용량이 너무 큽니다.(제한 1MB)");

    private final String label;

    Message(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
