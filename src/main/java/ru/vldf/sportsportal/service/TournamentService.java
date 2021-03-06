package ru.vldf.sportsportal.service;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vldf.sportsportal.domain.sectional.tournament.TeamEntity;
import ru.vldf.sportsportal.domain.sectional.tournament.TournamentEntity;
import ru.vldf.sportsportal.dto.general.AbstractIdentifiedLinkDTO;
import ru.vldf.sportsportal.dto.sectional.tournament.TournamentDTO;
import ru.vldf.sportsportal.dto.sectional.tournament.links.TeamLinkDTO;
import ru.vldf.sportsportal.mapper.sectional.tournament.TournamentMapper;
import ru.vldf.sportsportal.repository.tournament.TeamRepository;
import ru.vldf.sportsportal.repository.tournament.TournamentRepository;
import ru.vldf.sportsportal.service.general.AbstractSecurityService;
import ru.vldf.sportsportal.service.general.throwable.MethodArgumentNotAcceptableException;
import ru.vldf.sportsportal.service.general.throwable.ResourceNotFoundException;
import ru.vldf.sportsportal.service.tournament.RoundRobinGeneratorService;
import ru.vldf.sportsportal.util.ReflectionUtil;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Namednev Artem
 */
@Service
public class TournamentService extends AbstractSecurityService {

    private final RoundRobinGeneratorService roundRobinGeneratorService;

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;
    private final TeamRepository teamRepository;


    @Autowired
    public TournamentService(
            RoundRobinGeneratorService roundRobinGeneratorService,
            TournamentRepository tournamentRepository,
            TournamentMapper tournamentMapper,
            TeamRepository teamRepository
    ) {
        this.roundRobinGeneratorService = roundRobinGeneratorService;
        this.tournamentRepository = tournamentRepository;
        this.tournamentMapper = tournamentMapper;
        this.teamRepository = teamRepository;
    }


    private static MethodParameter generateMethodParameter() {
        return ReflectionUtil.methodParameter(TournamentService.class, "generate", new Class[]{TournamentDTO.class}, 0);
    }

    private static MethodParameter updateMethodParameter() {
        return ReflectionUtil.methodParameter(TournamentService.class, "update", new Class[]{Integer.class, TournamentDTO.class}, 1);
    }


    /**
     * Returns requested tournament by tournament identifier.
     *
     * @param id the tournament identifier.
     * @return requested tournament data.
     * @throws ResourceNotFoundException if tournament not found.
     */
    @Transactional(readOnly = true, rollbackFor = {ResourceNotFoundException.class})
    public TournamentDTO get(Integer id) throws ResourceNotFoundException {
        return tournamentMapper.toDTO(findById(id));
    }

    /**
     * Generate and save new round-robin tournament returns its identifier.
     *
     * @param tournamentDTO the new tournament details.
     * @return generated tournament identifier.
     * @throws MethodArgumentNotAcceptableException if method argument not acceptable.
     */
    @Transactional(rollbackFor = {MethodArgumentNotAcceptableException.class})
    public Integer generate(TournamentDTO tournamentDTO) throws MethodArgumentNotAcceptableException {
        generateCheck(tournamentDTO);
        return tournamentRepository.save(
                tournamentMapper.inject(
                        roundRobinGeneratorService.create(findTeams(tournamentDTO.getTeams())),
                        tournamentDTO
                )
        ).getId();
    }

    /**
     * Update and save tournament details by tournament identifier.
     *
     * @param id            the tournament identifier.
     * @param tournamentDTO the tournament new details.
     * @throws MethodArgumentNotAcceptableException if method argument not acceptable.
     * @throws ResourceNotFoundException            if tournament not found.
     */
    @Transactional(rollbackFor = {MethodArgumentNotAcceptableException.class, ResourceNotFoundException.class})
    public void update(Integer id, TournamentDTO tournamentDTO) throws MethodArgumentNotAcceptableException, ResourceNotFoundException {
        updateCheck(id, tournamentDTO);
        TournamentEntity injected = tournamentMapper.inject(findById(id), tournamentDTO);
        injected.getTeamParticipations().forEach(team -> team.setIsFixed(injected.getIsFixed()));
        tournamentRepository.save(injected);
    }


    private TournamentEntity findById(int id) throws ResourceNotFoundException {
        return tournamentRepository.findById(id).orElseThrow(ResourceNotFoundException.supplier(msg("sportsportal.tournament.Tournament.notExistById.message", id)));
    }


    private void generateCheck(TournamentDTO tournamentDTO) throws MethodArgumentNotAcceptableException {
        if (tournamentRepository.existsByBundleParentIsNullAndBundleTextLabel(tournamentDTO.getName())) {
            throw MethodArgumentNotAcceptableException.by(
                    generateMethodParameter(), tournamentDTO, ImmutableMap.of("name", msg("sportsportal.tournament.Tournament.validation.alreadyExistByName.message", tournamentDTO.getName()))
            );
        }
    }

    private void updateCheck(Integer tournamentId, TournamentDTO tournamentDTO) throws MethodArgumentNotAcceptableException {
        if (tournamentRepository.existsByBundleParentIsNullAndBundleTextLabelAndIdNot(tournamentDTO.getName(), tournamentId)) {
            throw MethodArgumentNotAcceptableException.by(
                    updateMethodParameter(), tournamentDTO, ImmutableMap.of("name", msg("sportsportal.tournament.Tournament.validation.alreadyExistByName.message", tournamentDTO.getName()))
            );
        }
    }


    private Collection<TeamEntity> findTeams(Collection<TeamLinkDTO> links) {
        return teamRepository.findAllById(links.stream().map(AbstractIdentifiedLinkDTO::getId).collect(Collectors.toSet()));
    }
}
