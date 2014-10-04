/*
 *   Project: Propity
 * 
 * File name: Translations.java
 *    Author: Matej Urbas [matej.urbas@gmail.com]
 * 
 *  Copyright © 2012 Matej Urbas
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package propity.i18n;

import java.util.Locale;

/**
 * This class provides translated strings for the Propity library.
 * <p>To use internationalised strings in Propity, import the
 * {@link propity.i18n.Translations#i18n(String) i18n} method like this:</p>
 * {@code import static propity.i18n.Translations.i18n; }
 * <p>and use it anywhere in your code, like this:</p>
 * {@code i18n("STRING_RESOURCE_KEY"); }
 * <p>Additional strings can be added to the {@code "propity/i18n/strings"}
 * bundle (see {@link propity.i18n.Translations#StringsBundle}).</p>
 *
 * @author Matej Urbas [matej.urbas@gmail.com]
 */
public final class Translations {

  // <editor-fold defaultstate="collapsed" desc="Disabled Constructor">
  private Translations() {
  }
  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Constants">
  /**
   * The path to the bundle which contains translations for Propity.
   */
  public static final String StringsBundle = "propity/i18n/strings";
  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Translated Strings">

  /**
   * Returns a string in the language of the current locale.
   * <p>It looks up the internationalised string based on the provided
   * key.</p>
   *
   * @param key the key of the string to fetch.
   * @return a string in the language of the current locale.
   */
  public static String i18n(String key) {
    return java.util.ResourceBundle.getBundle(StringsBundle).getString(key);
  }

  /**
   * Returns a formatted string in the language of the current locale.
   * <p>It looks up the internationalised string based on the provided
   * key.</p>
   *
   * @param key  the key of the <a href="http://download.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax">format specification string</a> to fetch.
   * @param args the arguments to the formatted string (the same as arguments to {@link String#format(java.lang.String, java.lang.Object[])}).
   * @return a string in the language of the current locale.
   */
  public static String i18n(String key, Object... args) {
    return String.format(java.util.ResourceBundle.getBundle(StringsBundle).getString(key), args);
  }

  /**
   * Returns a string in the language of the current locale.
   * <p>It looks up the internationalised string based on the provided
   * key.</p>
   *
   * @param locale the locale for which to return the internationalised string.
   * @param key    the key of the string to fetch.
   * @return a string in the language of the current locale.
   */
  public static String i18n(Locale locale, String key) {
    if (locale == null) {
      return i18n(key);
    } else {
      return java.util.ResourceBundle.getBundle(StringsBundle, locale).getString(key);
    }
  }

  /**
   * Returns a formatted string in the language of the current locale.
   * <p>It looks up the internationalised string based on the provided
   * key.</p>
   *
   * @param locale the locale for which to return the internationalised string.
   * @param key    the key of the <a href="http://download.oracle.com/javase/6/docs/api/java/util/Formatter.html#syntax">format specification string</a> to fetch.
   * @param args   the arguments to the formatted string (the same as arguments to {@link String#format(java.lang.String, java.lang.Object[])}).
   * @return a string in the language of the current locale.
   */
  public static String i18n(Locale locale, String key, Object... args) {
    if (locale == null) {
      return i18n(key, args);
    } else {
      return String.format(java.util.ResourceBundle.getBundle(StringsBundle, locale).getString(key), args);
    }
  }
  // </editor-fold>
}