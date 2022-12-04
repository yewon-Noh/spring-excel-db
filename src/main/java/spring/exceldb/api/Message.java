package spring.exceldb.api;

public enum Message {
    UPLOAD_EXCEL("업로드 되었습니다."),
    ERROR_UPLOAD_EXCEL("업로드 실패하였습니다.");

    private final String label;

    Message(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
