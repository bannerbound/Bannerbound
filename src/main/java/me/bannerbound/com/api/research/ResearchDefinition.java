package me.bannerbound.com.api.research;

import io.netty.buffer.ByteBuf;
import me.bannerbound.com.api.settlement.Era;
import me.bannerbound.com.api.settlement.FaithPath;
import me.bannerbound.com.api.settlement.GovernmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static me.bannerbound.com.codecs.BannerboundCodecs.STRING_LIST;

public record ResearchDefinition(
        String id,
        String name,
        String description,
        double cost,
        int x,
        int y,
        boolean autoUnlock,
        Era minAge,
        List<String> prerequisites,
        List<String> unlocksItems,
        List<String> unlocksFeatures,
        List<String> unlocksFlags,
        @Nullable GovernmentType governmentType,
        boolean requiresTribe,
        int heraldryPoints,
        boolean important,
        @Nullable FaithPath faithPath
) {

    public static final StreamCodec<ByteBuf, ResearchDefinition> STREAM_CODEC = StreamCodec.of(
            (buf, def) -> {
                ByteBufCodecs.STRING_UTF8.encode(buf, def.id());
                ByteBufCodecs.STRING_UTF8.encode(buf, def.name());
                ByteBufCodecs.STRING_UTF8.encode(buf, def.description());
                buf.writeDouble(def.cost());
                ByteBufCodecs.VAR_INT.encode(buf, def.x());
                ByteBufCodecs.VAR_INT.encode(buf, def.y());
                buf.writeBoolean(def.autoUnlock());
                ByteBufCodecs.VAR_INT.encode(buf, def.minAge().ordinal());
                STRING_LIST.encode(buf, def.prerequisites());
                STRING_LIST.encode(buf, def.unlocksItems());
                STRING_LIST.encode(buf, def.unlocksFeatures());
                STRING_LIST.encode(buf, def.unlocksFlags());
                //ByteBufCodecs.STRING_UTF8.encode(buf, def.ponderScene());

                ByteBufCodecs.VAR_INT.encode(buf,
                        def.governmentType() == null ? 0 : def.governmentType().ordinal() + 1);
                buf.writeBoolean(def.requiresTribe());
                ByteBufCodecs.VAR_INT.encode(buf, def.heraldryPoints());
                buf.writeBoolean(def.important());
                ByteBufCodecs.VAR_INT.encode(buf,
                        def.faithPath() == null ? 0 : def.faithPath().ordinal() + 1);

                // buf.writeBoolean(def.insight() != null);
                // if (def.insight() != null) InsightDefinition.STREAM_CODEC.encode(buf, def.insight());
            },
            buf -> new ResearchDefinition(
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    buf.readDouble(),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    buf.readBoolean(),
                    Era.fromOrdinalOrDefault(ByteBufCodecs.VAR_INT.decode(buf)),
                    STRING_LIST.decode(buf),
                    STRING_LIST.decode(buf),
                    STRING_LIST.decode(buf),
                    STRING_LIST.decode(buf),
                    // STRING_LIST.decode(buf),
                    // ByteBufCodecs.STRING_UTF8.decode(buf),
                    decodeGovernment(ByteBufCodecs.VAR_INT.decode(buf)),
                    buf.readBoolean(),
                    ByteBufCodecs.VAR_INT.decode(buf),
                    buf.readBoolean(),
                    decodeFaithPath(ByteBufCodecs.VAR_INT.decode(buf))
                    //buf.readBoolean() ? InsightDefinition.STREAM_CODEC.decode(buf) : null
            )
    );

    @org.jetbrains.annotations.Nullable
    private static FaithPath decodeFaithPath(int wire) {
        if (wire <= 0) return null;
        FaithPath[] vals = FaithPath.values();
        int idx = wire - 1;
        return idx < vals.length ? vals[idx] : null;
    }

    @org.jetbrains.annotations.Nullable
    private static GovernmentType decodeGovernment(int wire) {
        if (wire <= 0) return null;
        GovernmentType[] vals = GovernmentType.values();
        int idx = wire - 1;
        return idx < vals.length ? vals[idx] : null;
    }


    // ResearchDefinitions usually don't change. By overriding equals and hashCode, if we ever use it in a Set, hashing it will be faster.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearchDefinition that = (ResearchDefinition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
