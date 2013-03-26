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

package jp.sourceforge.hotchpotch.coopie.util;

import java.io.File;

public interface FileWalker {

    /*
     * ディレクトリへ入るかどうか
     * 
     * falseを返すとそれ以上深いディレクトリへはenterしない。
     */
    boolean shouldEnter(File dir);

    /*
     * ディレクトリへ入る。
     * 
     * shouldEnterでtrueを返すと直後に呼ばれる。
     */
    void enter(File dir);

    /*
     * ディレクトリから出る
     */
    void leave(File dir);

    /*
     * enterしたディレクトリ内のファイル。
     * 引数にはファイルを渡す。ディレクトリは渡さない。
     */
    void file(File file);

}
