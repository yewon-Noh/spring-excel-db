package spring.exceldb.exhibition.model;

import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor
@Entity
public class Exhibition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                // 전시 아이디

    @Column(length = 200)
    private String name;            // 전시명

    @Column(length = 1000)
    private String location;        // 전시 장소

    @Column(length = 20)
    private String category;        // 전시 구분(특별전, 테마전)

    @Temporal(TemporalType.DATE)
    private Date start;             // 전시 시작일

    @Temporal(TemporalType.DATE)
    private Date end;               // 전시 종료일

    @Column(length = 4000)
    private String exhibits;        // 전시품

    @Column(columnDefinition = "TEXT")
    private String content;         // 전시 요약

    public Exhibition() {

    }

    /**
     * DTO -> Entity
     * @param dto
     * @throws ParseException
     */
    public Exhibition(ExhibitionDTO dto) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        this.name = dto.getName();
        this.location = dto.getLocation();
        this.category = dto.getCategory();
        this.start = format.parse(dto.getStart());
        this.end = format.parse(dto.getEnd());
        this.exhibits = dto.getExhibits();
        this.content = dto.getContent();
    }

    /**
     * Entity -> DTO
     * @return
     */
    public ExhibitionDTO setExcelDTO() {
        return ExhibitionDTO.builder()
                .name(this.name)
                .location(this.location)
                .category(this.category)
                .start(String.valueOf(this.start))
                .end(String.valueOf(this.end))
                .exhibits(this.exhibits)
                .content(this.content)
                .build();
    }
}
