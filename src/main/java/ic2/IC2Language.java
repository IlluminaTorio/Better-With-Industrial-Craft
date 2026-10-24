

package ic2;

import ic2.IC2;
import ic2.mixin.LanguageAccessor;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import net.minecraft.core.lang.Language;
import net.minecraft.core.lang.LanguageSeeker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IC2Language {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"ic2");
    private static final String REMOVED_PACK_FILE = "ic2_ru_RU.zip";

    private IC2Language() {
    }

    public static void inject(Language language) {
        try {
            if (language == null || language.isDefault()) {
                return;
            }
            String id = language.getId();
            String path = "/assets/" + IC2.MOD_ID + "/lang/" + id + "/" + id + ".lang";
            InputStream in = IC2Language.class.getResourceAsStream(path);
            if (in == null) {
                return;
            }
            Properties props = new Properties();
            try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);){
                props.load(reader);
            }
            if (props.isEmpty()) {
                return;
            }
            Properties entries = ((LanguageAccessor)language).ic2$getEntries();
            entries.putAll((Map<?, ?>)props);
            LOGGER.info("Injected {} IC2 translations into language '{}'", (Object)props.size(), (Object)id);
        }
        catch (Exception e) {
            LOGGER.warn("Failed to inject IC2 translations", (Throwable)e);
        }
    }

    public static void removeInstalledLanguagePack() {
        try {
            File dir = LanguageSeeker.LANGUAGE_DIR;
            File ours = new File(dir, REMOVED_PACK_FILE);
            if (ours.exists() && ours.delete()) {
                LOGGER.info("Removed leftover IC2 language pack: {}", (Object)ours.getAbsolutePath());
            }
        }
        catch (Exception e) {
            LOGGER.warn("Failed to remove leftover IC2 language pack", (Throwable)e);
        }
    }
}

