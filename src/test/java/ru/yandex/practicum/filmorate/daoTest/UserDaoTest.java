package ru.yandex.practicum.filmorate.daoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDaoTest {

    private final UserDao userDao;
    private final FriendshipDao friendshipDao;
    private final UserService userService;
    private static User firstUser;
    private static User secondUser;
    private static User thridUser;

    @BeforeEach
    public void setUp() {
        firstUser = User.builder()
                .name("Qwer")
                .login("FirstUserewr")
                .email("user@yandex.ru")
                .birthday(LocalDate.of(1984, 7, 8))
                .build();
        secondUser = User.builder()
                .name("Name")
                .login("Login")
                .email("secondUser@yandex.ru")
                .birthday(LocalDate.of(1990, 10, 8))
                .build();
        thridUser = User.builder()
                .name("")
                .login("thridUser")
                .email("thridUser@yandex.ru")
                .birthday(LocalDate.of(1999, 11, 10))
                .build();

    }

    @Test
    public void addUserAndGetAllUsersAndGetUserByIdTest() {
        userDao.addUser(firstUser);
        userDao.addUser(secondUser);
        userDao.addUser(thridUser);
        Collection<User> users = userDao.getAllUsers();
        Optional<User> userOptional = Optional.ofNullable(userDao.getUserById(thridUser.getId()));

        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", thridUser.getId())
                                .hasFieldOrPropertyWithValue("name", "thridUser"));
        assertThat(users).contains(thridUser);
        assertThat(users).contains(secondUser);
    }

    @Test
    public void updateUserTest() {
        userDao.addUser(firstUser);
        User updateUser = User.builder()
                .id(firstUser.getId())
                .name("UpdateName")
                .login("UpdateLogin")
                .email("secondUser@yandex.ru")
                .birthday(LocalDate.of(1990, 10, 8))
                .build();
        userDao.updateUser(updateUser);
        Optional<User> testUpdateUser = Optional.ofNullable(userDao.getUserById(firstUser.getId()));
        assertThat(testUpdateUser)
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("name", "UpdateName"));
    }

    @Test
    public void addFriendAndGetCommonFriendsTest() {
        userDao.addUser(firstUser);
        userDao.addUser(secondUser);
        userDao.addUser(thridUser);
        friendshipDao.addFriend(firstUser.getId(), secondUser.getId());
        friendshipDao.addFriend(firstUser.getId(), thridUser.getId());
        friendshipDao.addFriend(secondUser.getId(), thridUser.getId());

        List<User> firstUserFriends = new ArrayList<>();
        firstUserFriends.add(secondUser);
        firstUserFriends.add(thridUser);

        List<User> secondUserFriends = new ArrayList<>();
        secondUserFriends.add(thridUser);

        List<User> commonFriends = new ArrayList<>();
        commonFriends.add(thridUser);

        assertThat(userDao.getAllUserFriends(firstUser.getId())).hasSize(firstUserFriends.size());
        assertThat(userDao.getAllUserFriends(firstUser.getId())).contains(secondUser);
        assertThat(userDao.getAllUserFriends(firstUser.getId())).contains(thridUser);
        assertThat(userDao.getAllUserFriends(secondUser.getId())).hasSize(secondUserFriends.size());
        assertThat(userDao.getAllUserFriends(secondUser.getId())).contains(thridUser);
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId())).hasSize(commonFriends.size());
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId())).contains(thridUser);
    }

    @Test
    public void deleteFriend() {
        userDao.addUser(firstUser);
        userDao.addUser(secondUser);
        friendshipDao.addFriend(firstUser.getId(), secondUser.getId());
        friendshipDao.deleteFriend(firstUser.getId(), secondUser.getId());

        assertThat(userDao.getAllUserFriends(firstUser.getId())).hasSize(0);
    }

    @Test
    public void deleteUserTest() {
        userDao.addUser(firstUser);
        userDao.addUser(secondUser);
        userDao.removeUser(secondUser.getId());
        Collection<User> listUsers = userDao.getAllUsers();
        assertThat(listUsers).hasSize(1);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> userDao.getUserById(2)
        );
    }
}
