package pl.lodz.p.edu.grs.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.grs.controller.borrow.BorrowDto;
import pl.lodz.p.edu.grs.model.Borrow;
import pl.lodz.p.edu.grs.model.Game;
import pl.lodz.p.edu.grs.model.user.User;
import pl.lodz.p.edu.grs.repository.BorrowRepository;
import pl.lodz.p.edu.grs.service.BorrowService;
import pl.lodz.p.edu.grs.service.GameService;
import pl.lodz.p.edu.grs.service.UserService;
import pl.lodz.p.edu.grs.util.BorrowUtil;
import pl.lodz.p.edu.grs.util.GameUtil;
import pl.lodz.p.edu.grs.util.UserUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BorrowServiceImplTest {

    @Mock
    private BorrowRepository borrowRepository;

    @Mock
    private GameService gameService;

    @Mock
    private UserService userService;

    private BorrowService borrowService;

    @Before
    public void setUp() throws Exception {
        this.borrowService = new BorrowServiceImpl(borrowRepository, gameService, userService);
    }


    @Test
    public void shouldReturnPageOfBorrows() throws Exception {
        //given
        Pageable pageable = new PageRequest(0, 10);
        Borrow borrow = new Borrow();

        List<Borrow> borrows = Collections.singletonList(borrow);
        PageImpl<Borrow> borrowsPage = new PageImpl<>(borrows);

        when(borrowRepository.findAll(pageable))
                .thenReturn(borrowsPage);

        //when
        Page<Borrow> result = borrowService.findAll(pageable);

        //then
        assertThat(result)
                .isEqualTo(borrowsPage);
        assertThat(result.getContent())
                .containsExactly(borrow);
        assertThat(result.getTotalElements())
                .isEqualTo(borrows.size());
    }

    @Test
    public void shouldReturnUserPageOfBorrows() throws Exception {
        //given
        Pageable pageable = new PageRequest(0, 10);
        Borrow borrow = new Borrow();
        User user = UserUtil.mockUser();
        borrow.setUser(user);
        List<Borrow> borrows = Collections.singletonList(borrow);
        PageImpl<Borrow> borrowsPage = new PageImpl<>(borrows);

        when(borrowRepository.findBorrowsByUser_Email(pageable, user.getEmail()))
                .thenReturn(borrowsPage);


        //when
        Page<Borrow> result = borrowService.findUserBorrows(pageable, user.getEmail());

        //then

        assertThat(result)
                .isEqualTo(borrowsPage);
        assertThat(result.getContent())
                .containsExactly(borrow);
        assertThat(result.getTotalElements())
                .isEqualTo(borrows.size());
    }

    @Test
    public void shouldAddBorrow() throws Exception {
        //given
        Borrow borrow = BorrowUtil.mockBorrow();
        User user = UserUtil.mockUser();
        Game game = GameUtil.mockGame();
        borrow.setBorrowedGames(Collections.singletonList(game));
        borrow.setUser(user);

        when(userService.findByEmail(UserUtil.EMAIL))
                .thenReturn(user);
        when(gameService.getGame(1L))
                .thenReturn(game);
        when(borrowRepository.save(borrow))
                .thenReturn(borrow);

        //when
        Borrow result = borrowService.addBorrow(Collections.singletonList(1L), UserUtil.EMAIL);

        //then
        verify(borrowRepository)
                .save(borrow);
    }
}