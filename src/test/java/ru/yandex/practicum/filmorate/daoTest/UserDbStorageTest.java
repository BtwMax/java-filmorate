package ru.yandex.practicum.filmorate.daoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

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
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final FriendshipDao friendshipDao;
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
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        userDbStorage.addUser(thridUser);
        Collection<User> users = userDbStorage.getAllUsers();
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUserById(thridUser.getId()));

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
        userDbStorage.addUser(firstUser);
        User updateUser = User.builder()
                .id(firstUser.getId())
                .name("UpdateName")
                .login("UpdateLogin")
                .email("secondUser@yandex.ru")
                .birthday(LocalDate.of(1990, 10, 8))
                .build();
        userDbStorage.updateUser(updateUser);
        Optional<User> testUpdateUser = Optional.ofNullable(userDbStorage.getUserById(firstUser.getId()));
        assertThat(testUpdateUser)
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("name", "UpdateName"));
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

        assertThat(userDbStorage.getAllUserFriends(firstUser.getId())).hasSize(firstUserFriends.size());
        assertThat(userDbStorage.getAllUserFriends(firstUser.getId())).contains(secondUser);
        assertThat(userDbStorage.getAllUserFriends(firstUser.getId())).contains(thridUser);
        assertThat(userDbStorage.getAllUserFriends(secondUser.getId())).hasSize(secondUserFriends.size());
        assertThat(userDbStorage.getAllUserFriends(secondUser.getId())).contains(thridUser);
        assertThat(friendshipDao.getCommonFriends(firstUser.getId(), secondUser.getId())).hasSize(commonFriends.size());
        assertThat(friendshipDao.getCommonFriends(firstUser.getId(), secondUser.getId())).contains(thridUser);
    }

    @Test
    public void deleteFriend() {
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        friendshipDao.addFriend(firstUser.getId(), secondUser.getId());
        friendshipDao.deleteFriend(firstUser.getId(), secondUser.getId());

        assertThat(userDbStorage.getAllUserFriends(firstUser.getId())).hasSize(0);
    }

    @Test
    public void deleteUserTest() {
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(secondUser);
        userDbStorage.removeUser(secondUser.getId());
        Collection<User> listUsers = userDbStorage.getAllUsers();
        assertThat(listUsers).hasSize(1);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> userDbStorage.getUserById(2)
        );
    }
}
