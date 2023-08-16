/*
 * Copyright 2010 manhole
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.internal.CollectionsUtil;
import jp.sourceforge.hotchpotch.coopie.util.Text;

/**
 * @author manhole
 */
class DefaultFixedLengthLineBuilder implements FixedLengthLineBuilder {

    private final List<Req> requests_ = CollectionsUtil.newArrayList();

    @Override
    public void write(final CharSequence element, final int beginIndex, final int endIndex) {
        final Req req = new Req(element, beginIndex, endIndex);
        requests_.add(req);
    }

    @Override
    public String buildString() {
        final List<Req> requests = CollectionsUtil.newArrayList();
        requests.addAll(requests_);
        Collections.sort(requests, (o1, o2) -> o1.beginIndex_ - o2.beginIndex_);

        final StringBuilder sb = new StringBuilder();
        int pos = 0;
        for (final Req req : requests) {
            while (pos < req.beginIndex_) {
                sb.append(' ');
                pos++;
            }

            final int len = Text.length(req.element_);
            sb.append(req.element_);
            pos += len;

            while (pos < req.endIndex_) {
                sb.append(' ');
                pos++;
            }
        }
        return sb.toString();
    }

    private static class Req {

        private final CharSequence element_;
        private final int beginIndex_;
        private final int endIndex_;

        public Req(final CharSequence element, final int beginIndex, final int endIndex) {
            element_ = element;
            beginIndex_ = beginIndex;
            endIndex_ = endIndex;
        }
    }

}
