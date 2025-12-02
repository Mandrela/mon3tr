package su.maibat.mon3tr.commands;

// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Mockito;

// import su.maibat.mon3tr.chat.Chat;
// import su.maibat.mon3tr.db.DeadlineQuery;
// import su.maibat.mon3tr.db.SQLiteLinker;
// import su.maibat.mon3tr.db.UserQuery;
// import su.maibat.mon3tr.db.exceptions.DeadlineNotFound;
// import su.maibat.mon3tr.db.exceptions.UserNotFound;
// import static org.mockito.ArgumentMatchers.anyInt;

public final class MyDeadlinesCommandTest {
    // private final SQLiteLinker linker = Mockito.mock(SQLiteLinker.class);
    // private final Chat chat = Mockito.mock(Chat.class);
    // private final MyDeadlinesCommand show = new MyDeadlinesCommand(linker);

    // @Test
    // @DisplayName("Shows for user with deadlines")
    // void correctShowTest() throws UserNotFound, DeadlineNotFound {
    //     long chatId = 1234;
    //     Mockito.when(chat.getChatId()).thenReturn(chatId);

    //     UserQuery user = new UserQuery(0, chatId);
    //     Mockito.when(linker.getUserByChatId(chatId)).thenReturn(user);

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

    //     Mockito.when(linker.getDeadlinesForUser(0)).thenReturn(deadlinesForUser);


    //     assertDoesNotThrow(() -> show.execute(chat), "Should not throw");

    //     Mockito.verify(chat, Mockito.times(1)).getChatId();
    //     Mockito.verify(linker, Mockito.times(1)).getUserByChatId(chatId);
    //     Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(0);

    //     ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
    //     Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

    //     assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
    //     String answer = answerCaptor.getValue();

    //     for (int i = 0; i < 4; i++) {
    //         assertTrue(answer.contains(deadlinesForUser[i].getName()),
    //                 "Text contains all deadlines");
    //     }
    // }

    // @Test
    // @DisplayName("Shows for user without deadlines")
    // void emptyUserTest() throws UserNotFound, DeadlineNotFound {
    //     long chatId = 12345;
    //     Mockito.when(chat.getChatId()).thenReturn(chatId);

    //     UserQuery user = new UserQuery(1, chatId);
    //     Mockito.when(linker.getUserByChatId(chatId)).thenReturn(user);

    //     Mockito.when(linker.getDeadlinesForUser(1)).thenThrow(DeadlineNotFound.class);

    //     assertDoesNotThrow(() -> show.execute(chat), "Should not throw");

    //     Mockito.verify(chat, Mockito.times(1)).getChatId();
    //     Mockito.verify(linker, Mockito.times(1)).getUserByChatId(chatId);
    //     Mockito.verify(linker, Mockito.times(1)).getDeadlinesForUser(1);

    //     ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
    //     Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

    //     assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
    //     String answer = answerCaptor.getValue();

    //     assertEquals("You have not any deadlines", answer);
    // }

    // @Test
    // @DisplayName("Shows for new user")
    // void userNotFoundTest() throws UserNotFound, DeadlineNotFound {
    //     long chatId = 4444;
    //     Mockito.when(chat.getChatId()).thenReturn(chatId);

    //     Mockito.when(linker.getUserByChatId(chatId)).thenThrow(UserNotFound.class);

    //     assertDoesNotThrow(() -> show.execute(chat), "Should not throw");

    //     Mockito.verify(chat, Mockito.times(2)).getChatId();
    //     Mockito.verify(linker, Mockito.times(1)).getUserByChatId(chatId);
    //     Mockito.verify(linker, Mockito.never()).getDeadlinesForUser(anyInt());

    //     ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
    //     Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

    //     assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
    //     String answer = answerCaptor.getValue();

    //     assertEquals("You have not any deadlines", answer);
    // }
}
