package info.faceland.loot.api.managers;

import java.util.List;

public interface NameManager {

    List<String> getPrefixes();

    List<String> getSuffixes();

    String getRandomPrefix();

    String getRandomSuffix();

    void addPrefix(String s);

    void addSuffix(String s);

    void removePrefix(String s);

    void removeSuffix(String s);

}
