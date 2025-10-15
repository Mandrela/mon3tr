package su.maibat.mon3tr;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.business.SetBusinessAccountProfilePhoto;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPaidMedia;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.api.methods.stickers.ReplaceStickerInSet;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumbnail;
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


public class SuppliedTelegramClient<IT extends Serializable, InnMethod extends BotApiMethod<IT>> implements TelegramClient {
    private InnMethod lastMethod = null;

    public <T extends Serializable, Method extends BotApiMethod<T>> Method getLastMethod() {
        return (Method)lastMethod;
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> T
            execute(Method method) throws TelegramApiException {
        lastMethod = (InnMethod) method;
        return (T)"UNSPECIFIED PURPOSE STRING";
    }


    // GARBAGE
    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T>
            executeAsync(Method method) throws TelegramApiException { return new CompletableFuture<T>(); }

    public Message execute(SendDocument sendDocument) throws TelegramApiException { System.out.println("Doc"); return new Message(); }

    public Message execute(SendPhoto sendPhoto) throws TelegramApiException { System.out.println("Pho"); return new Message(); }

    public Boolean execute(SetWebhook setWebhook) throws TelegramApiException { System.out.println("Web"); return true; }

    public Message execute(SendVideo sendVideo) throws TelegramApiException { System.out.println("Vid"); return new Message(); }

    public Message execute(SendVideoNote sendVideoNote) throws TelegramApiException { System.out.println("VidN"); return new Message(); }

    public Message execute(SendSticker sendSticker) throws TelegramApiException { System.out.println("Sck"); return new Message(); }

    public Boolean execute(SetBusinessAccountProfilePhoto setBusinessAccountProfilePhoto) throws TelegramApiException { System.out.println("Bis"); return true; }

    public Message execute(SendAudio sendAudio) throws TelegramApiException { return new Message();}
    public Message execute(SendVoice sendVoice) throws TelegramApiException { return new Message();}

    public List<Message> execute(SendMediaGroup sendMediaGroup) throws TelegramApiException { return new ArrayList<Message>(); }

    public List<Message> execute(SendPaidMedia sendPaidMedia) throws TelegramApiException { return new ArrayList<Message>(); }

    public Boolean execute(SetChatPhoto setChatPhoto) throws TelegramApiException { return true; }

    public Boolean execute(AddStickerToSet addStickerToSet) throws TelegramApiException { return true; }

    public Boolean execute(ReplaceStickerInSet replaceStickerInSet) throws TelegramApiException { return true; }

    public Boolean execute(SetStickerSetThumbnail setStickerSetThumbnail) throws TelegramApiException { return true; }

    public Boolean execute(CreateNewStickerSet createNewStickerSet) throws TelegramApiException { return true; }

    public File execute(UploadStickerFile uploadStickerFile) throws TelegramApiException { return new File(); }

    public Serializable execute(EditMessageMedia editMessageMedia) throws TelegramApiException { return new ArrayList(); }

    public java.io.File downloadFile(File file) throws TelegramApiException { return new java.io.File("CREATED FROM SUPPLIED TELEGRAM"); }

    public InputStream downloadFileAsStream(File file) throws TelegramApiException { return new ByteArrayInputStream(new byte[2]); }

    public Message execute(SendAnimation sendAnimation) throws TelegramApiException { return new Message(); }

    public CompletableFuture<Message> executeAsync(SendDocument sendDocument) { return new CompletableFuture<Message>(); }

    public CompletableFuture<Message> executeAsync(SendPhoto sendPhoto) { return new CompletableFuture<Message>(); }

    public CompletableFuture<Boolean> executeAsync(SetWebhook setWebhook) { return new CompletableFuture<Boolean>(); }

    public CompletableFuture<Message> executeAsync(SendVideo sendVideo) { return new CompletableFuture<Message>(); }

    public CompletableFuture<Message> executeAsync(SendVideoNote sendVideoNote) { return new CompletableFuture<Message>(); }

    public CompletableFuture<Message> executeAsync(SendSticker sendSticker) { return new CompletableFuture<Message>(); }

    public CompletableFuture<Message> executeAsync(SendAudio sendAudio) { return new CompletableFuture<Message>(); }

    public CompletableFuture<Message> executeAsync(SendVoice sendVoice) { return new CompletableFuture<Message>(); }

    public CompletableFuture<List<Message>> executeAsync(SendMediaGroup sendMediaGroup) { return new CompletableFuture<List<Message>>(); }

    public CompletableFuture<List<Message>> executeAsync(SendPaidMedia sendPaidMedia) { return new CompletableFuture<List<Message>>(); }

    public CompletableFuture<Boolean> executeAsync(SetChatPhoto setChatPhoto) { return new CompletableFuture<Boolean>(); }

    public CompletableFuture<Boolean> executeAsync(AddStickerToSet addStickerToSet) { return new CompletableFuture<Boolean>(); }

    public CompletableFuture<Boolean> executeAsync(ReplaceStickerInSet replaceStickerInSet) { return new CompletableFuture<Boolean>(); }

    public CompletableFuture<Boolean> executeAsync(SetStickerSetThumbnail setStickerSetThumbnail) { return new CompletableFuture<Boolean>(); }

    public CompletableFuture<Boolean> executeAsync(CreateNewStickerSet createNewStickerSet) { return new CompletableFuture<Boolean>(); }

    public CompletableFuture<File> executeAsync(UploadStickerFile uploadStickerFile) { return new CompletableFuture<File>(); }

    public CompletableFuture<Serializable> executeAsync(EditMessageMedia editMessageMedia) { return new CompletableFuture<Serializable>(); }

    public CompletableFuture<Message> executeAsync(SendAnimation sendAnimation) { return new CompletableFuture<Message>(); }

    public CompletableFuture<Boolean> executeAsync(SetBusinessAccountProfilePhoto setBusinessAccountProfilePhoto) { return new CompletableFuture<Boolean>(); }

    public CompletableFuture<java.io.File> downloadFileAsync(File file) { return new CompletableFuture<java.io.File>(); }

    public CompletableFuture<InputStream> downloadFileAsStreamAsync(File file) { return new CompletableFuture<InputStream>(); }
}
