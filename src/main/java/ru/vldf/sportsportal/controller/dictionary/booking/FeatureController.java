package ru.vldf.sportsportal.controller.dictionary.booking;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vldf.sportsportal.dto.pagination.PageDTO;
import ru.vldf.sportsportal.dto.pagination.filters.generic.PageDividerDTO;
import ru.vldf.sportsportal.dto.sectional.booking.FeatureDTO;
import ru.vldf.sportsportal.service.dictionary.booking.FeatureService;
import ru.vldf.sportsportal.service.general.throwable.ResourceNotFoundException;

/**
 * @author Namednev Artem
 */
@RestController
@Api(tags = {"Dictionary Feature"})
@RequestMapping("${api.path.booking.dict.feature}")
public class FeatureController {

    private FeatureService featureService;

    @Autowired
    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }


    @GetMapping("/byId/{id}")
    @ApiOperation("получить особенность по идентификатору")
    public FeatureDTO get(Integer id) throws ResourceNotFoundException {
        return featureService.get(id);
    }

    @GetMapping("/byCode/{code}")
    @ApiOperation("получить особенность по коду")
    public FeatureDTO get(String code) throws ResourceNotFoundException {
        return featureService.get(code);
    }

    @GetMapping("/list")
    @ApiOperation("получить страницу с особенностями")
    public PageDTO<FeatureDTO> getList(PageDividerDTO pageDividerDTO) {
        return featureService.getList(pageDividerDTO);
    }
}
