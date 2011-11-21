package jp.sourceforge.hotchpotch.coopie.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.ArrayMap;
import org.t2framework.commons.util.CollectionsUtil;

public class BeanMapTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void sizeZero() throws Throwable {
        // ## Arrange ##
        final Map<String, Object> map = new BeanMap(new Object());

        // ## Act ##

        // ## Assert ##
        assertEquals(0, map.size());
        assertEquals(true, map.isEmpty());
        assertEquals(false, map.containsKey("aaa"));
    }

    @Test
    public void putAndGet() throws Throwable {
        // ## Arrange ##
        final Foo obj = new Foo();
        final Map<String, Object> map = new BeanMap(obj);

        // ## Act ##
        // ## Assert ##
        assertEquals(2, map.size());
        assertEquals(false, map.isEmpty());
        assertEquals(true, map.containsKey("aaa"));
        assertEquals(true, map.containsKey("bbBbb"));

        assertEquals(null, map.get("aaa"));
        obj.setAaa("123");
        assertEquals("123", map.get("aaa"));
        assertEquals("123", map.put("aaa", "4"));
        assertEquals("4", obj.getAaa());

        assertEquals(null, obj.getBbBbb());
        assertEquals(null, map.put("bbBbb", 987));
        assertEquals(987, obj.getBbBbb().intValue());
        assertEquals(987, map.put("bbBbb", 6));
        assertEquals(6, obj.getBbBbb().intValue());
    }

    @Test
    public void put_invalidKey() throws Throwable {
        // ## Arrange ##
        final Foo obj = new Foo();
        final Map<String, Object> map = new BeanMap(obj);

        // ## Act ##
        // ## Assert ##
        try {
            map.put("ddd", "123");
            fail();
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
        }
    }

    @Test
    public void get_invalidKey() throws Throwable {
        // ## Arrange ##
        final Foo obj = new Foo();
        final Map<String, Object> map = new BeanMap(obj);

        // ## Act ##
        // ## Assert ##
        try {
            map.get("ddd");
            fail();
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
        }
    }

    @Test
    public void put_badType_lenient() throws Throwable {
        // ## Arrange ##
        final Foo obj = new Foo();
        final Map<String, Object> map = new BeanMap(obj);

        // ## Act ##
        // ## Assert ##
        assertEquals(null, map.put("bbBbb", "987"));
        assertEquals(987, obj.getBbBbb().intValue());
        assertEquals(987, map.put("bbBbb", 6));
        assertEquals(6, obj.getBbBbb().intValue());
    }

    @Test
    public void put_badType_notlenient() throws Throwable {
        // ## Arrange ##
        final Bar obj = new Bar();
        final BeanMap map = new BeanMap(obj);
        map.setLenient(false);

        // ## Act ##
        // ## Assert ##
        try {
            map.put("bbBbb", "987");
            fail();
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
        }
        try {
            map.put("bbBbb", new BigDecimal("987"));
            fail();
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
        }

        {
            // これはsetできる
            map.put("ccc", new java.sql.Timestamp(9999L));
            assertEquals(9999L, obj.getCcc().getTime());
        }
    }

    @Test
    public void putAll() throws Throwable {
        // ## Arrange ##
        final Bar obj = new Bar();
        final Map<String, Object> map = new BeanMap(obj);

        // ## Act ##
        final ArrayMap<String, Object> a = CollectionsUtil.newArrayMap();
        a.put("aaa", "1111");
        a.put("ccc", new Date(66667L));
        map.putAll(a);

        // ## Assert ##
        assertEquals(3, map.size());
        assertEquals(false, map.isEmpty());
        assertEquals(true, map.containsKey("aaa"));
        assertEquals(true, map.containsKey("bbBbb"));

        assertEquals("1111", map.get("aaa"));
        assertEquals("1111", obj.getAaa());
        assertEquals(null, obj.getBbBbb());
        assertEquals(66667L, obj.getCcc().getTime());
    }

    @Test
    public void containsValue() throws Throwable {
        // ## Arrange ##
        final Bar obj = new Bar();
        final Map<String, Object> map = new BeanMap(obj);

        // ## Act ##
        // ## Assert ##
        assertEquals(true, map.containsValue(null));
        assertEquals(false, map.containsValue(333));

        obj.setBbBbb(333);
        assertEquals(true, map.containsValue(333));
    }

    @Test
    public void values1() throws Throwable {
        // ## Arrange ##
        final Bar obj = new Bar();
        final Map<String, Object> map = new BeanMap(obj);

        // ## Act ##
        // ## Assert ##
        assertEquals(3, map.size());
        final Collection<Object> c = map.values();
        assertEquals(3, c.size());
        assertEquals(false, c.isEmpty());

        final Iterator<Object> it = c.iterator();
        assertEquals(null, it.next());
        assertEquals(null, it.next());
        assertEquals(null, it.next());
        assertEquals(false, it.hasNext());
    }

    @Test
    public void values2() throws Throwable {
        // ## Arrange ##
        final Bar obj = new Bar();
        final Map<String, Object> map = new BeanMap(obj);
        obj.setAaa("123");
        obj.setBbBbb(3210);
        obj.setCcc(new Date(88888L));

        // ## Act ##
        // ## Assert ##
        assertEquals(3, map.size());
        final Collection<Object> c = map.values();
        assertEquals(3, c.size());
        assertEquals(false, c.isEmpty());

        assertEquals(false, c.remove(123));
        assertEquals(false, c.remove(4567));
        assertEquals(true, c.contains("123"));
        assertEquals(true, c.contains(3210));
        assertEquals(true, c.contains(new Date(88888L)));
    }

    @Test
    public void keySet() throws Throwable {
        // ## Arrange ##
        final Bar obj = new Bar();
        final Map<String, Object> map = new BeanMap(obj);

        // ## Act ##
        // ## Assert ##
        assertEquals(3, map.size());
        final Set<String> keySet = map.keySet();
        assertEquals(false, keySet.remove("zzzz"));
        assertEquals(true, keySet.contains("aaa"));
        assertEquals(true, keySet.contains("bbBbb"));
        assertEquals(true, keySet.contains("ccc"));
    }

    @Test
    public void entrySet() throws Throwable {
        // ## Arrange ##
        final Bar obj = new Bar();
        final Map<String, Object> map = new BeanMap(obj);
        obj.setBbBbb(321);
        final Date date = new Date();
        final long time = date.getTime();
        obj.setCcc(date);

        final HashMap<String, Object> m = CollectionsUtil.newHashMap();

        // ## Act ##
        final Set<Entry<String, Object>> set = map.entrySet();
        assertEquals(false, set.isEmpty());
        assertEquals(3, set.size());
        for (final Entry<String, Object> entry : set) {
            final Object prev = m.put(entry.getKey(), entry.getValue());
            assertEquals(null, prev);
        }

        // ## Assert ##
        assertEquals(3, m.size());
        assertEquals(true, m.containsKey("aaa"));
        assertEquals(null, m.get("aaa"));

        assertEquals(true, m.containsKey("bbBbb"));
        assertEquals(321, m.get("bbBbb"));

        assertEquals(true, m.containsKey("ccc"));
        assertEquals(new Date(time), m.get("ccc"));
    }

    @Test
    public void remove() throws Throwable {
        // ## Arrange ##
        final Foo obj = new Foo();
        final Map<String, Object> map = new BeanMap(obj);

        // ## Act ##
        // ## Assert ##
        try {
            map.remove("aaa");
            fail();
        } catch (final UnsupportedOperationException e) {
            logger.debug(e.getMessage());
        }

    }

    public static class Foo {

        private String aaa_;
        private Integer bbBbb_;
        private String ddd_;

        public String getAaa() {
            return aaa_;
        }

        public void setAaa(final String aaa) {
            aaa_ = aaa;
        }

        public Integer getBbBbb() {
            return bbBbb_;
        }

        public void setBbBbb(final Integer bbBbb) {
            bbBbb_ = bbBbb;
        }

        private String getDdd() {
            return ddd_;
        }

        private void setDdd(final String ddd) {
            ddd_ = ddd;
        }

    }

    public static class Bar extends Foo {

        private Long ccc_;

        public Date getCcc() {
            if (ccc_ == null) {
                return null;
            }
            return new Date(ccc_);
        }

        public void setCcc(final Date date) {
            ccc_ = date.getTime();
        }

    }

}
