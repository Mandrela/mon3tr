package su.maibat.mon3tr.bot;

// import static su.maibat.mon3tr.Main.DEBUG;


public final class Bot implements BotBackend {
    private static final char PREFIX = '/';

    public char getCommandPrefix() {
        return PREFIX;
    }

    public void process(final int userId, final String command) throws BotException {

    }

    // public static final String NAME = "mon3tr";
    // private static final char PREFIX = '/';

    // private static final int THREADS_CORE_POOL_SIZE = 2;
    // private static final int THREADS_MAX_POOL_SIZE = 8;
    // private static final int THREADS_QUEUE_SIZE = 2 * THREADS_MAX_POOL_SIZE;

    // private static final long THREADS_IDLE_TIMEOUT = 2;
    // private static final TimeUnit THREADS_TIME_UNIT = TimeUnit.MINUTES;


    // private final BlockingQueue<Runnable> jobQueue = new ArrayBlockingQueue<>(THREADS_QUEUE_SIZE);
    // private final ConcurrentHashMap<Long, MessageSink> sinkMap = new ConcurrentHashMap<>();
    // private final TelegramClient telegramClient;
    // private final Map<String, Command> commands;
    // private final Command defaultCommand;
    // private final Executor executor;

    // /**
    //  * @param token Telegram token -- KEEP SAFE, DO NOT SHARE IT.
    //  * @param commandsArgument Map were key is a name of a command in bot's interface
    //  * and value is an instance of Command implementing class.
    //  * @param defaultCommandArgument The default command which will be executed if incorrect
    //  * command supplied.
    //  */
    // public Bot(final String token, final Map<String, Command> commandsArgument,
    //             /*final BlockingQueue<Pair<int, String>> responseQueue,*/
    //             final Command defaultCommandArgument) {
    //     telegramClient = new OkHttpTelegramClient(token);
    //     commands = Collections.unmodifiableMap(commandsArgument);
    //     defaultCommand = defaultCommandArgument;
    //     executor = new ThreadPoolExecutor(THREADS_CORE_POOL_SIZE, THREADS_MAX_POOL_SIZE,
    //         THREADS_IDLE_TIMEOUT, THREADS_TIME_UNIT, jobQueue);
    // }

    // // public Bot(final Map<String, Command> commandsArgument, final BlockingQueue<Pair<int,
    // // String>> responseQueue,
    // //     final Command defaultCommandArgument, final Command registerCommandArgument) {
    // // }


    // /**
    //  * Parses string to array of arguments, where the first argument is a command itself without
    //  * a prefix.
    //  * @param textString Input String
    //  * @return Array of arguments where the first one is a prefix-less command
    //  */
    // private static String[] parseCommand(final String textString) {
    //     if (textString.charAt(0) == PREFIX) {
    //         String[] result = textString.split(" ");
    //         result[0] = result[0].substring(1);
    //         return result;
    //     }
    //     return null;
    // }

    // public TelegramClient getTelegramClient() {
    //     return telegramClient;
    // }

    // @Override
    // public void consume(final Update update) {
    //     Message message = update.getMessage();

    //     if (message != null && message.hasText()) {
    //         Long chatId = message.getChatId();
    //         String[] arguments = parseCommand(message.getText());

    //         if (arguments != null) {
    //             System.out.println(DEBUG + "Initializing new command");
    //             sinkMap.computeIfPresent(chatId,
    //                 (key, value) -> {
    //                     value.interrupt(); return null;
    //                 });

    //             TelegramChat telegramChat = new TelegramChat(chatId, telegramClient);
    //             telegramChat.addMessages(Arrays.copyOfRange(arguments, 1,
    // arguments.length));
    //             telegramChat.freeze();
    //             sinkMap.put(chatId, telegramChat);

    //             Command commandToExecute = commands.getOrDefault(arguments[0].
    // toLowerCase(),
    //                 defaultCommand);

    //             executor.execute(() -> {
    //                 commandToExecute.execute(telegramChat);
    //                 sinkMap.computeIfPresent(chatId, (key, value) -> {
    //                     value.interrupt();
    //                     return null;
    //                 });
    //             });
    //         } else if (sinkMap.containsKey(chatId)) {
    //             System.out.println(DEBUG + "Passing message");
    //             sinkMap.get(chatId).addMessage(message.getText());
    //         } else {
    //             System.out.println(DEBUG + "Executing default command");
    //             executor.execute(() ->
    //                 defaultCommand.execute(new TelegramChat(chatId, telegramClient)));
    //         }
    //     }
    // }

    // Map<userId, State> map = HashMap<>();

    // // State = {"command": Command pointer, "data": String[]}

    // @Override
    // public void process(final int userId, final String command) throws BotException {
    //     if (userId <= 0) {
    //         registerCommand.execute(userId, command.split(" "), null);
    //     } else {
    //         if (isCommand(command)) {
    //             map.add((command or defaultCommand).execute(userId, command.split(" ")[1:],
    // null));
    //         } else if (map.has(userId)){
    //             map.add(map[userId].command, command.split(" ")[1:], map[userId].data);
    //         } else {
    //             defaultCommand.execute(userId, null, null);
    //         }
    //     }
    // }
}
