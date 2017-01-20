package org.gdl2;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.gdl2.datatypes.CodePhrase;
import org.gdl2.deserializers.CodePhraseDeserializer;
import org.gdl2.deserializers.ExpressionItemDeserializer;
import org.gdl2.expression.ExpressionItem;
import org.gdl2.model.DataBinding;
import org.gdl2.model.GuideDefinition;
import org.gdl2.model.Guideline;
import org.gdl2.model.Rule;
import org.gdl2.serializers.*;

import java.util.List;
import java.util.Map;

/**
 * To serialize and deserialize guidelines in Gdl2 json format.
 */
public class Gdl2 {

    public static Guideline fromGdl2(String guidelineInGDL2Format) {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(ExpressionItem.class, new ExpressionItemDeserializer())
                .registerTypeAdapter(CodePhrase.class, new CodePhraseDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(guidelineInGDL2Format, Guideline.class);
    }

    public static String toGdl2(Guideline guideline) {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(List.class, new ListSerializer())
                .registerTypeHierarchyAdapter(Map.class, new MapSerializer())
                .registerTypeAdapter(DataBinding.class, new DataBindingSerializer())
                .registerTypeAdapter(CodePhrase.class, new CodePhraseSerializer())
                .registerTypeAdapter(GuideDefinition.class, new GuideDefinitionSerializer())
                .registerTypeAdapter(Rule.class, new RuleSerializer())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        return gson.toJson(guideline);
    }
}
