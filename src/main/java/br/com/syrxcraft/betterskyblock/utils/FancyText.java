package br.com.syrxcraft.betterskyblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FancyText {

    public String text = "";
    public String hoverText = null;
    public String runCommandActionText = null;
    public String suggestCommandAction = null;
    public String tellRawCommand = null;
    public String lastColor = "";

    public boolean recentChanged = true;

    public FancyText() {

    }

    public FancyText(String text) {
        this.text = text;
    }

    public FancyText(List<String> textList) {
        this.text = String.join("\n", textList);
    }

    public FancyText(String text, String hoverText) {
        this.text = text;
        this.hoverText = hoverText;
    }

    public FancyText(String text, String hoverText, String runCommandActionText) {
        this.text = text;
        this.hoverText = hoverText;
        this.runCommandActionText = runCommandActionText;
    }

    public FancyText(String text, String hoverText, String commandActionText, boolean suggestionCommand) {
        this.text = text;
        this.hoverText = hoverText;
        if (suggestionCommand){
            this.suggestCommandAction = commandActionText;
        }else {
            this.runCommandActionText = commandActionText;
        }
    }

    /**
     *   Seta o texto dessa FancyMessage, por padrão, o texto é vazio "";
     */
    public FancyText setText(String text) {
        setRecentChanged();
        this.text = text;
        return this;
    }
    public FancyText setText(List<String> textList){
        setRecentChanged();
        this.text = String.join("\n", textList);
        return this;
    }

    /**
     *   Seta o texto dessa FancyMessage, por padrão, o texto é vazio "";
     */
    public FancyText addText(String text) {
        setRecentChanged();
        this.text += text;
        return this;
    }
    public FancyText addText(List<String> textList){
        setRecentChanged();
        this.text += String.join("\n", textList);
        return this;
    }

    /**
     *   Seta o a HoverMessage dessa FancyMessage;
     */
    public FancyText setHoverText(String hoverText) {
        setRecentChanged();
        this.hoverText = hoverText;
        return this;
    }
    public FancyText setHoverText(List<String> hoverTextList){
        setRecentChanged();
        this.hoverText = String.join("\n", hoverTextList);
        return this;
    }

    /**
     *   Seta o a RunCommand dessa FancyMessage;
     *   (Comando executado quando o jogador clica na mensagem)
     */
    public FancyText setRunCommandActionText(String runCommandActionText) {
        setRecentChanged();
        this.runCommandActionText = runCommandActionText;
        return this;
    }

    /**
     *   Seta o a SuggestCommand dessa FancyMessage;
     *   (Comando sugerido quando o jogador clica na mensagem)
     */
    public FancyText setSuggestCommandAction(String suggestCommandAction) {
        setRecentChanged();
        this.suggestCommandAction = suggestCommandAction;
        return this;
    }

    public void setRecentChanged(){
        if (!recentChanged) recentChanged = true;
    }


    /**
     *   Retorna a parte do comando fancytext dessa FancyMessage!
     *
     *   Vale salientar que esse método nao retorna o comando correto do fancytext, apenas uma parte dele!
     *
     *   Use as funções do FancyText.class ao invés dessa!
     */
    public String getTellRawString(){

        if (tellRawCommand != null && !recentChanged){
            return this.tellRawCommand;
        }
        recentChanged = false;

        this.text = fixTellrawTextColors(this.text);
        this.lastColor = getLastColor(this.text);

        StringBuilder tellRaw = new StringBuilder("{\"text\":\"" + this.text + "\"");
        if (this.hoverText != null){
            this.hoverText = fixTellrawTextColors(this.hoverText);
            tellRaw.append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + this.hoverText + "\"}");
        }
        if (runCommandActionText != null){
            tellRaw.append(",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + fixBrackets(this.runCommandActionText) + "\"}");
        }else if (suggestCommandAction != null){
            tellRaw.append(",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + fixBrackets(this.suggestCommandAction) + "\"}");
        }
        tellRaw.append("}");
        return (this.tellRawCommand = tellRaw.toString());
    }

    /**
     *   Recebe um player como argumento
     *
     *   Monta o comando para esses FancyTextElement
     *   e envia a mensagem para o jogador!
     */
    public void sendTo(CommandSender commandSender){
        if(commandSender instanceof Player){
            String command = tellRawCommandBuilder(this);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + commandSender.getName() + " " + command);
        }else {
            commandSender.sendMessage(textOnlyTextBuilder(this));
        }
    }

    // ------------------------------------------------------------------------------------------------------
    // Métodos Estáticos para se entregar as mensagens
    // ------------------------------------------------------------------------------------------------------


    /**
     *   Recebe um player como argumento e uma List de FancyTextElement
     *
     *   Monta o comando para esses FancyTextElement
     *   e envia a mensagem para o jogador!
     */
    public static void sendTo(CommandSender commandSender, List<FancyText> texts){
        if(commandSender instanceof Player){
            String command = tellRawCommandBuilder(texts);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + commandSender.getName() + " " + command);
        }else {
            commandSender.sendMessage(textOnlyTextBuilder(texts));
        }
    }

    /**
     *   Monta o comando para esses FancyTextElement
     *   e envia a mensagem para todos os jogadores!
     */
    public static void tellRawBroadcast(List<FancyText> texts){
        String command = tellRawCommandBuilder(texts);
        for (Player player : Bukkit.getOnlinePlayers()){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + command);
        }
        Bukkit.getConsoleSender().sendMessage(textOnlyTextBuilder(texts));
    }

    /**
     *   Recebe uma List de FancyTextElement e retorna
     *   o comando (String) corresponde a lista!
     */
    public static String tellRawCommandBuilder(List<FancyText> texts){
        StringBuilder command = new StringBuilder("[\"\"");
        String previousLastColor = "";
        for (FancyText aFancyTextElement : texts){
            aFancyTextElement.text = previousLastColor + aFancyTextElement.text;
            command.append("," + aFancyTextElement.getTellRawString());
            previousLastColor = aFancyTextElement.getLastTextColor();

        }
        return command.append("]").toString();
    }

    /**
     *   Recebe uma List de FancyTextElement e retorna
     *   o comando (String) corresponde a lista!
     */
    public static String textOnlyTextBuilder(List<FancyText> texts){
        StringBuilder text = new StringBuilder();
        for (FancyText aFancyTextElement : texts){
            text.append(aFancyTextElement.text);
        }
        return text.toString();
    }


    // ------------------------------------------------------------------------------------------------------
    // Faz a mesma coisa que os de cima, contudo, recebe itens diretamente, nao necessariamente em uma lista!
    // Um vetor simples por exemplo!
    // ------------------------------------------------------------------------------------------------------


    public static void sendTo(CommandSender commandSender, FancyText... texts){
        if(commandSender instanceof Player){
            String command = tellRawCommandBuilder(texts);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + commandSender.getName() + " " + command);
        }else {
            commandSender.sendMessage(textOnlyTextBuilder(texts));
        }
    }

    public static void tellRawBroadcast(FancyText... texts){
        String command = tellRawCommandBuilder(texts);
        for (Player player : Bukkit.getOnlinePlayers()){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + command);
        }
        Bukkit.getConsoleSender().sendMessage(textOnlyTextBuilder(texts));
    }

    public static String tellRawCommandBuilder(FancyText... texts){
        StringBuilder command = new StringBuilder("[\"\"");
        for (FancyText aFancyTextElement : texts){
            command.append("," + aFancyTextElement.getTellRawString());
        }
        command.append("]");
        return command.toString();
    }

    /**
     *   Recebe uma List de FancyTextElement e retorna
     *   o comando (String) corresponde a lista!
     */
    public static String textOnlyTextBuilder(FancyText... texts){
        StringBuilder text = new StringBuilder();
        for (FancyText aFancyTextElement : texts){
            text.append(aFancyTextElement.text);
        }
        return text.toString();
    }

    // ------------------------------------------------------------------------------------------------------
    // TellRaw Fixer
    // ------------------------------------------------------------------------------------------------------

    private String getLastTextColor() {
        return lastColor;
    }

    private String getLastColor(String text){
        return ChatColor.getLastColors(text);
    }

    private String fixTellrawTextColors(String theText){
        String[] allWords = theText.split(" ");
        String lastColor = "";
        for (int i = 0; i < allWords.length; i++){
            if (!allWords[i].trim().isEmpty()){
                allWords[i] = lastColor + allWords[i];
                lastColor = ChatColor.getLastColors(allWords[i]);
            }
        }
        return fixBrackets(String.join(" ",allWords));
    }

    private String fixBrackets(String theText){
        return theText.replaceAll("\"", "'");    }
}