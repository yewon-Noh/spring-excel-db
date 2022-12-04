package spring.exceldb.exhibition.service;

import spring.exceldb.exhibition.model.ExhibitionDTO;

import java.util.List;

public interface ExhibitionService {
    public List<ExhibitionDTO> findAll();
    public List<ExhibitionDTO> findByCategory(String category);
}
