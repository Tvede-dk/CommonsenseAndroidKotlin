@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package csense.android.tools.resources

import android.support.annotation.*
import kotlin.reflect.*


/**
 * A dynamic resource lookup; all you have to do is to supply the various R values.
 *
 */

class DynamicResources(
        private val RString: KClass<*>,
        private val RDrawable: KClass<*>,
        private val RLayout: KClass<*>,
        private val RColors: KClass<*>,
//        private val RXml: Any,
//        private val RFonts: Any,
        private val RRaw: KClass<*>,
        private val RDimensions: KClass<*>,
        private val RStyle: KClass<*>,
//        private val RMenu: Any,
//        private val RTransitions: Any,
        private val RStyleable: KClass<*>,
//        private val RPlural: Any,
        private val RAttr: KClass<*>,
        private val RAnimator: KClass<*>,
        private val RAnim: KClass<*>

) {


    val allStrings by lazy {
        RString.mapFieldsToNameId(DynamicResourceType::StringResource)
    }

    val allDrawables by lazy {
        RDrawable.mapFieldsToNameId(DynamicResourceType::DrawableResource)
    }

    val allLayouts by lazy {
        RLayout.mapFieldsToNameId(DynamicResourceType::LayoutResource)
    }

    val allColors by lazy {
        RColors.mapFieldsToNameId(DynamicResourceType::ColorResource)
    }

//    val allXmlResource by lazy {
//        RXml.mapFieldsToNameId(DynamicResourceType::XmlResource)
//    }
//
//    val allFonts by lazy {
//        RFonts.mapFieldsToNameId(DynamicResourceType::FontResource)
//    }

    val allRawResources by lazy {
        RRaw.mapFieldsToNameId(DynamicResourceType::RawResource)
    }

    val allDimensions by lazy {
        RDimensions.mapFieldsToNameId(DynamicResourceType::DimensionResource)
    }

    val allStyles by lazy {
        RStyle.mapFieldsToNameId(DynamicResourceType::StyleResource)
    }
//    val allMenus by lazy {
//        RMenu.mapFieldsToNameId(DynamicResourceType::MenuResource)
//    }

    val allAnimators by lazy {
        RAnimator.mapFieldsToNameId(DynamicResourceType::AnimatorResource)
    }

//    val allTransistions by lazy {
//        RTransitions.mapFieldsToNameId(DynamicResourceType::TransitionResource)
//    }

    val allStyleable by lazy {
        RStyleable.mapFieldsToNameId(DynamicResourceType::StyleableResource)
    }

    val allAttr by lazy {
        RAttr.mapFieldsToNameId(DynamicResourceType::AttrResource)
    }

//    val allPlural by lazy {
//        RPlural.mapFieldsToNameId(DynamicResourceType::PluralResResource)
//    }

    val allAnims by lazy {
        RAnim.mapFieldsToNameId(DynamicResourceType::AnimResource)
    }

    inline fun <T : Any> KClass<*>.mapFieldsToNameId(ctor: Function2<String, Int, T?>): List<T> {
        return this.java.fields.mapNotNull {
            val extractedValue = it.get(this) as? Int ?: return@mapNotNull null
            ctor(it.name, extractedValue)
        }
    }
}

sealed class DynamicResourceType(val name: String) {

    abstract val id: Int

    class LayoutResource(name: String, @LayoutRes override val id: Int) : DynamicResourceType(name)
    class StringResource(name: String, @StringRes override val id: Int) : DynamicResourceType(name)
    class DrawableResource(name: String, @DrawableRes override val id: Int) : DynamicResourceType(name)
    class ColorResource(name: String, @ColorRes override val id: Int) : DynamicResourceType(name)
    class RawResource(name: String, @RawRes override val id: Int) : DynamicResourceType(name)

    class XmlResource(name: String, @XmlRes override val id: Int) : DynamicResourceType(name)
    class FontResource(name: String, @FontRes override val id: Int) : DynamicResourceType(name)

    class DimensionResource(name: String, @DimenRes override val id: Int) : DynamicResourceType(name)
    class StyleResource(name: String, @StyleRes override val id: Int) : DynamicResourceType(name)
    class MenuResource(name: String, @MenuRes override val id: Int) : DynamicResourceType(name)

    class TransitionResource(name: String, @TransitionRes override val id: Int) : DynamicResourceType(name)
    class StyleableResource(name: String, @StyleableRes override val id: Int) : DynamicResourceType(name)

    class PluralResResource(name: String, @PluralsRes override val id: Int) : DynamicResourceType(name)
    class AttrResource(name: String, @PluralsRes override val id: Int) : DynamicResourceType(name)

    class AnimatorResource(name: String, @PluralsRes override val id: Int) : DynamicResourceType(name)
    class AnimResource(name: String, @PluralsRes override val id: Int) : DynamicResourceType(name)


}

