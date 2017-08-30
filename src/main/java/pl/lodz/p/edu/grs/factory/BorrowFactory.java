package pl.lodz.p.edu.grs.factory;

import org.springframework.stereotype.Component;
import pl.lodz.p.edu.grs.model.Borrow;
import pl.lodz.p.edu.grs.model.Game;
import pl.lodz.p.edu.grs.model.user.User;

import java.util.List;

@Component
public class BorrowFactory {

    public Borrow create(List<Game> borrowed, User user) {
       Borrow borrow = new Borrow(borrowed, user);
       borrow.setTotalPrice(borrowed.stream().mapToDouble(Game::getPrice).sum());
       return borrow;
    }
}
