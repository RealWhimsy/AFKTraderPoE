package de.realwhimsy.afktraderpoe.datamodel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParseUtil {
    public static final String dummyRegex = "Hi";
    public static final Pattern dummyPattern = Pattern.compile(dummyRegex);
    public static final String itemBuyRegex = "\\@From (.+)<*.*>*: Hi, I would like to buy your (.*) listed for (\\d+) (.+) in (.+) \\(stash";
    public static final Pattern itemBuyPattern = Pattern.compile(itemBuyRegex);

    public static boolean matchesItemBuyPattern(String message) {
        Matcher matcher = itemBuyPattern.matcher(message);
        return matcher.find();
    }

    public static Transaction getTransactionForItem(String buyMessage) {
        Matcher matcher = itemBuyPattern.matcher(buyMessage);

        if (matcher.find()) {
            var buyer = matcher.group(1);
            var itemName = matcher.group(2);
            var currencyAmount = matcher.group(3);
            var currencyName = matcher.group(4);
            var league = matcher.group(5);

            return new Transaction(buyer, new Price(Integer.parseInt(currencyAmount), currencyName), league, new Item(itemName, 1));
        } else {
            return null;
        }
    }
}
