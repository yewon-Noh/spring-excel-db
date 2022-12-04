package spring.exceldb.exhibition.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExhibitionDTO {
    private String name;
    private String location;
    private String category;
    private String start;
    private String end;
    private String exhibits;
    private String content;
}
