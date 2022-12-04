package spring.exceldb.exhibition.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.exceldb.exhibition.model.Exhibition;
import spring.exceldb.exhibition.model.ExhibitionDTO;
import spring.exceldb.exhibition.repository.ExhibitionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExhibitionServiceImpl implements ExhibitionService {

    private final ExhibitionRepository repository;

    @Autowired
    ExhibitionServiceImpl(ExhibitionRepository repository) {
        this.repository = repository;
    }

    /**
     * 모든 전시회 조회
     * @return
     */
    @Override
    public List<ExhibitionDTO> findAll() {
        List<ExhibitionDTO> list = new ArrayList<>();
        for (Exhibition exhibition : repository.findAll()) {
            list.add(exhibition.setExcelDTO());
        }
        return list;
    }

    /**
     * 카테고리에 맞는 전시회 조회
     * @param category
     * @return
     */
    @Override
    public List<ExhibitionDTO> findByCategory(String category) {
        List<ExhibitionDTO> list = new ArrayList<>();
        for (Exhibition exhibition : repository.findByCategory(category)) {
            list.add(exhibition.setExcelDTO());
        }
        return list;
    }
}
