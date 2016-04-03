package angelhack.org.ghostspace;

import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DelayedPrinter {

    private static Map<String, Integer> charDelay;

    static {
        charDelay = new HashMap<>();
        charDelay.put(".", 150);
        charDelay.put("-", 120);
        charDelay.put(",", 100);
        charDelay.put(":", 120);
        charDelay.put("?", 150);
        charDelay.put("!", 150);
    }

    public static void printText(final Word word, final TextView textView) {
        Random random = new Random(System.currentTimeMillis());

        int currentRandOffset = random.nextInt(word.offset);
        boolean addOrSubtract = random.nextBoolean();
        long finalDelay = addOrSubtract ? word.delayBetweenSymbols + currentRandOffset : word.delayBetweenSymbols - currentRandOffset;
        if (finalDelay < 0) finalDelay = 0;

        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (word.currentCharacterIndex == 0) textView.setText("");
                String charAt = String.valueOf(word.word.charAt(word.currentCharacterIndex));
                ++word.currentCharacterIndex;
                textView.setText(textView.getText() + charAt);
                if (word.currentCharacterIndex >= word.word.length()) {
                    word.currentCharacterIndex = 0;
                    return;
                }
                printText(word, textView);
            }
        }, finalDelay);
    }

    public static void printText(final Word word, final TextView textView, final Button button) {
        Random random = new Random(System.currentTimeMillis());
        int currentRandOffset = random.nextInt(word.offset);
        boolean addOrSubtract = random.nextBoolean();
        long finalDelay = addOrSubtract ? word.delayBetweenSymbols + currentRandOffset : word.delayBetweenSymbols - currentRandOffset;
        if (finalDelay < 0) finalDelay = 0;

        String charAt = String.valueOf(word.word.charAt(word.currentCharacterIndex));
        if (charDelay.containsKey(charAt)) {
            finalDelay += charDelay.get(charAt);
        }

        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (word.currentCharacterIndex == 0) {
                    button.setEnabled(false);
                    textView.setText("");
                }
                String charAt = String.valueOf(word.word.charAt(word.currentCharacterIndex));

                if (word.currentCharacterIndex == 0 && charAt.equals(".")) {
                    String localCharAt = String.valueOf(word.word.charAt(word.currentCharacterIndex + 1));
                    if (localCharAt.equals(".")) {
                        localCharAt = String.valueOf(word.word.charAt(word.currentCharacterIndex + 2));
                        if (localCharAt.equals(".")) {
                            charAt = "...";
                            ++word.currentCharacterIndex;
                            ++word.currentCharacterIndex;
                        }
                    }
                }

                ++word.currentCharacterIndex;
                textView.setText(textView.getText() + charAt);
                if (word.currentCharacterIndex >= word.word.length()) {
                    word.currentCharacterIndex = 0;
                    button.setEnabled(true);
                    return;
                }

                printText(word, textView, button);
            }
        }, finalDelay);
    }

    public static class Word {

        private long delayBetweenSymbols;
        private String word;
        private int offset;
        private int currentCharacterIndex;

        public Word(long delayBetweenSymbols, int offset, String word) {
            if (delayBetweenSymbols < 0) throw new IllegalArgumentException("Delay can't be < 0");
            this.delayBetweenSymbols = delayBetweenSymbols;
            this.word = word;
            this.currentCharacterIndex = 0;
            this.setOffset(offset);
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }
}
