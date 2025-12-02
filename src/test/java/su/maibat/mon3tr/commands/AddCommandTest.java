package su.maibat.mon3tr.commands;

// import java.util.stream.Stream;

// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// // import org.junit.jupiter.api.Test;
// // import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.Arguments;
// import org.junit.jupiter.params.provider.MethodSource;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Mockito;

// import su.maibat.mon3tr.chat.Chat;
// import su.maibat.mon3tr.db.DataBaseLinker;
// import su.maibat.mon3tr.db.SQLiteLinker;
// import su.maibat.mon3tr.db.UserQuery;
// import su.maibat.mon3tr.db.exceptions.UserNotFound;

public final class AddCommandTest {
//     private final Chat chat = Mockito.mock(Chat.class);
//     private final DataBaseLinker linker = Mockito.mock(SQLiteLinker.class);
//     private final DeadlineAddCommand add = new DeadlineAddCommand(linker);


//     @BeforeEach
//     void setUp() throws UserNotFound {
//         long chatId = 1234;
//         Mockito.when(chat.getChatId()).thenReturn(chatId);

//         UserQuery user = new UserQuery(1, 1234);
//         user.setLimit(32);
//         Mockito.when(linker.getUserByChatId(1234)).thenReturn(user);
//     }

//     // @Test
//     @DisplayName("Add without arguments")
//     void incorrectAddTest()  {
//         Mockito.when(chat.getAllMessages()).thenReturn(new String[] {});

//         assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

//         Mockito.verify(chat, Mockito.times(1)).getAllMessages();

//         ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
//         Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

//         assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
//         String answer = answerCaptor.getValue();

//         assertEquals("Something went wrong, try again with input some arguments", answer);
//     }

//     // @Test
//     @DisplayName("Add with correct arguments")
//     void testWithArgs() {
//         String[] data = {"Deadline", "20.12.2025"};
//         Mockito.when(chat.getAllMessages()).thenReturn(data);

//         assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

//         Mockito.verify(chat, Mockito.times(1)).getAllMessages();

//         ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
//         Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

//         assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
//         String answer = answerCaptor.getValue();

//         assertEquals("Deadline added successfully", answer);
//     }

//     // @Test
//     @DisplayName("Add with reverse order of correct arguments")
//     void testWithReverseArgs() {
//         String[] data = {"20.12.2025", "Deadline"};
//         Mockito.when(chat.getAllMessages()).thenReturn(data);

//         assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

//         Mockito.verify(chat, Mockito.times(1)).getAllMessages();

//         ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);
//         Mockito.verify(chat, Mockito.times(1)).sendAnswer(answerCaptor.capture());

//         assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
//         String answer = answerCaptor.getValue();

//         assertEquals("Deadline added successfully", answer);
//     }


//     // @ParameterizedTest(name = "Date set")
//     @MethodSource("dateArgs")
//     @DisplayName("Add with incorrect date")
//     void incorrectDateTest(final String arg, final Boolean isValid) {
//         //Возможен второй аргумент


//         String[] data = {"SecondDeadline", arg};
//         Mockito.when(chat.getAllMessages()).thenReturn(data);

//         assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

//         ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);

//         Mockito.verify(chat, Mockito.atLeastOnce()).sendAnswer(answerCaptor.capture());

//         assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
//         String answer = answerCaptor.getValue();

//         assertEquals(!isValid, answer.contains("Please enter correct date"),
//                 "Date " + arg + " is not valid");


//     }

//     static Stream<Arguments> dateArgs() {
//         return Stream.of(
//             Arguments.of("17/8/2025", true),
//                 Arguments.of("20;12;2025", false),
//                 Arguments.of("31.02.2025", true),
//                 Arguments.of("0/12/2025", true),
//                 Arguments.of("20.15.2025", true),
//                 Arguments.of("20.0.2025", true),
//                 Arguments.of("20.12", false),
//                 Arguments.of("20.12.", false),
//                 Arguments.of("abracadabra", false),
//                 Arguments.of("", false)
//         );
//     }

//     // @ParameterizedTest(name = "Name set")
//     @MethodSource("nameArgs")
//     @DisplayName("Add with incorrect deadline name")
//     void incorrectNameTest(final String arg, final Boolean isValid) {

//         String[] data = {arg, "20.12.2025"};
//         Mockito.when(chat.getAllMessages()).thenReturn(data);

//         assertDoesNotThrow(() -> add.execute(chat), "Should not throw");

//         ArgumentCaptor<String> answerCaptor = ArgumentCaptor.forClass(String.class);

//         Mockito.verify(chat, Mockito.atLeastOnce()).sendAnswer(answerCaptor.capture());

//         assertEquals(1, answerCaptor.getAllValues().size(), "Should answer only once");
//         String answer = answerCaptor.getValue();

//         assertEquals(!isValid, answer.contains("Please enter valid name"),
//                 "Name " + arg + "is not valid");

//     }

//     static Stream<Arguments> nameArgs() {
//         return Stream.of(
//             Arguments.of("Al6ed0_was_here", true),
//             Arguments.of("20.12.2025", false),
//             Arguments.of("", false)
//     );
// }
}
