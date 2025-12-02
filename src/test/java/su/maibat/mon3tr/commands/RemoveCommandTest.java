package su.maibat.mon3tr.commands;

// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// //import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Mockito;

// import su.maibat.mon3tr.chat.Chat;
// import su.maibat.mon3tr.db.DeadlineQuery;
// import su.maibat.mon3tr.db.SQLiteLinker;
// import su.maibat.mon3tr.db.UserQuery;
// import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
// import su.maibat.mon3tr.db.exceptions.UserNotFound;

public final class RemoveCommandTest {
    // private final Chat chat = Mockito.mock(Chat.class);
    // private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    // private final DeadlineRemoveCommand remove = new DeadlineRemoveCommand(linker);

    // //Все аргументы верные
    // //Неверный id (нет такого дедлайна)
    // //Неверный id (нет такого дедлайна у данного пользователя)
    // //Неверный пользователь (нет пользователя) - X
    // //Неверный ввод
    // //Отсутствие аргументов

    // @BeforeEach
    // void setUp() throws UserNotFound, DeadlineNotFound {

    //     long chatId = 1234;
    //     Mockito.when(chat.getChatId()).thenReturn(chatId);

    //     UserQuery user = new UserQuery(1, 1234);
    //     Mockito.when(linker.getUserByChatId(1234)).thenReturn(user);
    //     Mockito.when(linker.getUserById(1)).thenReturn(user);

    //     String[] data = {"4"};
    //     Mockito.when(chat.getAllMessages()).thenReturn(data);

    //     long burnTime = 18082011;

    //     DeadlineQuery dl1 = new DeadlineQuery();
    //     dl1.setName("first");
    //     dl1.setExpireTime(burnTime);
    //     dl1.setOwnerId(0);

    //     DeadlineQuery dl2 = new DeadlineQuery();
    //     dl2.setName("second");
    //     dl2.setExpireTime(burnTime);
    //     dl2.setOwnerId(0);

    //     DeadlineQuery dl3 = new DeadlineQuery();
    //     dl3.setName("third");
    //     dl3.setExpireTime(burnTime);
    //     dl3.setOwnerId(0);

    //     DeadlineQuery dl4 = new DeadlineQuery();
    //     dl4.setName("chotyri");
    //     dl4.setExpireTime(burnTime);
    //     dl4.setOwnerId(0);

    //     DeadlineQuery[] deadlinesForUser = {dl1, dl2, dl3, dl4};

    //     Mockito.when(linker.getDeadlinesForUser(1)).thenReturn(deadlinesForUser);
    // }


    // //@Test
    // @DisplayName("Remove with correct arguments")
    // void correctRemoveTest() throws DeadlineNotFound {

    //     DeadlineQuery deadline = new DeadlineQuery();
    //     deadline.setOwnerId(1);

    //     assertDoesNotThrow(() -> remove.execute(chat));

    //     Mockito.verify(chat, Mockito.times(1)).getAllMessages();
    //     Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(1);

    //     Mockito.verify(linker, Mockito.times(1)).removeDeadline(3);

    //     ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
    //     Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

    //     assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
    //     String answer = answerCaptor.getValue();

    //     assertEquals("You have closed this gestalt!!!", answer);

    // }

    // //@Test
    // @DisplayName("Remove with illegal arguments")
    // void illegalArgumentsTest() throws InterruptedException, DeadlineNotFound {
    //     String[] data = {"abracadabra"};
    //     Mockito.when(chat.getAllMessages()).thenReturn(data);
    //     Mockito.when(chat.getMessage()).thenReturn("4");

    //     assertDoesNotThrow(() -> remove.execute(chat));


    //     Mockito.verify(chat, Mockito.times(1)).getAllMessages();
    //     Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(1);

    //     Mockito.verify(linker, Mockito.times(1)).removeDeadline(3);

    //     ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
    //     Mockito.verify(chat, Mockito.times(2)).sendAnswer(answerCaptor.capture());

    //     assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
    //     String answer = answerCaptor.getValue();

    //     assertEquals("Please enter a valid deadline id (number)", answer);
    // }

    // //@Test
    // @DisplayName("Remove without arguments")
    // void emptyArgumentsTest() throws InterruptedException, DeadlineNotFound {
    //     String[] data = {};
    //     Mockito.when(chat.getAllMessages()).thenReturn(data);
    //     Mockito.when(chat.getMessage()).thenReturn("4");

    //     assertDoesNotThrow(() -> remove.execute(chat));
    //     Mockito.verify(chat, Mockito.times(1)).getAllMessages();
    //     Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(1);

    //     Mockito.verify(linker, Mockito.times(1)).removeDeadline(3);

    //     ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
    //     Mockito.verify(chat, Mockito.times(2)).sendAnswer(answerCaptor.capture());

    //     assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
    //     String answer = answerCaptor.getValue();

    //     assertEquals("Something went wrong, try again with input some arguments", answer);
    // }


}
