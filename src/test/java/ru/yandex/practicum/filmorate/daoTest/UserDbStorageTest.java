package ru.yandex.practicum.filmorate.daoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/test/java/ru/yandex/practicum/filmorate/TestResources/testSchema.sql",
        "file:src/test/java/ru/yandex/practicum/filmorate/TestResources/testData.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final FriendshipDao friendshipDao;
    private User firstUser;
    private User secondUser;
    private User thridUser;

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
        userDbStorage.addUser(firstUser);
        User getFirstUser = userDbStorage.getUserById(1);
        Assertions.assertNotNull(getFirstUser);
        Assertions.assertEquals(1, userDbStorage.getAllUsers().size());
        Assertions.assertEquals(firstUser, getFirstUser);
    }

    @Test
    public void addUserWithEmptyNameTest() {
        userDbStorage.addUser(thridUser);
        Assertions.assertEquals("thridUser", thridUser.getName());
    }

    @Test
    public void updateUserTest() {
        userDbStorage.addUser(firstUser);
        User updateUser = User.builder()
                .id(firstUser.getId())
                .name("UpdateName")
                .login("UpdateLogin")
                .email("secondUser@yandex.ru")
                .birthday(LocalDate.of(1990, 10, 8))
                .build();
        userDbStorage.updateUser(updateUser);
        User updatedUser = userDbStorage.getUserById(1);
        Assertions.assertEquals(updateUser, updatedUser);
    }

    @Test
    public void deleteUserTest() {
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        userDbStorage.removeUser(secondUser.getId());
        Assertions.assertEquals(1, userDbStorage.getAllUsers().size());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> userDbStorage.getUserById(2)
        );
    }

    @Test
    public void addFriendAndGetCommonFriendsTest() {
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        userDbStorage.addUser(thridUser);
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

        Assertions.assertEquals(firstUserFriends, userDbStorage.getAllUserFriends(firstUser.getId()));
        Assertions.assertEquals(secondUserFriends, userDbStorage.getAllUserFriends(secondUser.getId()));
        Assertions.assertEquals(commonFriends, friendshipDao.getCommonFriends(firstUser.getId(), secondUser.getId()));
    }

    @Test
    public void deleteFriend() {
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        friendshipDao.addFriend(firstUser.getId(), secondUser.getId());
        friendshipDao.deleteFriend(firstUser.getId(), secondUser.getId());

        Assertions.assertEquals(0, userDbStorage.getAllUserFriends(firstUser.getId()).size());
    }
}
