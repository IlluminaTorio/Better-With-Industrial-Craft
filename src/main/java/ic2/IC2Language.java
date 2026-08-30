

package ic2;

import ic2.IC2;
import ic2.mixin.LanguageAccessor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.core.lang.Language;
import net.minecraft.core.lang.LanguageSeeker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IC2Language {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"ic2");
    private static final String PACK_FILE = "ic2_ru_RU.zip";
    private static final String PACK_LANG_ID = "ru_RU";
    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");

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

    public static void installLanguagePack() {
        try {
            File dir = LanguageSeeker.LANGUAGE_DIR;
            if (!dir.exists() && !dir.mkdirs()) {
                return;
            }
            File ours = new File(dir, PACK_FILE);
            boolean hasOtherPack = false;
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.getName().toLowerCase().endsWith(".zip") || file.equals(ours) || !PACK_LANG_ID.equals(IC2Language.readPackId(file))) continue;
                    hasOtherPack = true;
                    break;
                }
            }
            if (hasOtherPack) {
                if (ours.exists() && ours.delete()) {
                    LOGGER.info("Removed IC2 language pack: a different '{}' pack is installed", (Object)PACK_LANG_ID);
                }
                return;
            }
            if (ours.exists()) {
                return;
            }
            try (InputStream in = IC2Language.class.getResourceAsStream("/assets/" + IC2.MOD_ID + "/langpack/ic2_ru_RU.zip");){
                if (in != null) {
                    Files.copy(in, ours.toPath(), new CopyOption[0]);
                    LOGGER.info("Installed IC2 language pack to {}", (Object)ours.getAbsolutePath());
                }
            }
        }
        catch (Exception e) {
            LOGGER.warn("Failed to install IC2 language pack", (Throwable)e);
        }
    }

    
    private static String readPackId(File zipFile) {
        try (ZipFile zip = new ZipFile(zipFile);){
            ZipEntry entry = zip.getEntry("lang_info.json");
            if (entry == null) {
                String string = null;
                return string;
            }
            StringBuilder sb = new StringBuilder();
            try (InputStreamReader reader = new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8);){
                int n;
                char[] buf = new char[256];
                while ((n = reader.read(buf)) > 0) {
                    sb.append(buf, 0, n);
                }
            }
            Matcher m = ID_PATTERN.matcher(sb);
            String string = m.find() ? m.group(1) : null;
            return string;
        }
        catch (IOException e) {
            return null;
        }
    }
}

