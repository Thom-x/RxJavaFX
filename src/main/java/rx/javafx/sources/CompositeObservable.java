/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.javafx.sources;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import rx.Observable;
import rx.observables.JavaFxObservable;

/**
 * A CompositeObservable can merge multiple Observables that can be added/removed at any time,
 * affecting all Subscribers regardless of when they subscribed. This is especailly helpful for merging
 * multiple UI event sources.
 * @param <T>
 */
public final class CompositeObservable<T> {

    private final ObservableList<Observable<T>> sources;
    private final Observable<T> observable;

    public CompositeObservable() {
        this(-1);
    }

    public CompositeObservable(int initialCapacity) {
        sources = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

        Observable<T> observable = JavaFxObservable.fromObservableList(sources)
                .switchMap(list -> Observable.from(list).flatMap((Observable<T> obs) -> obs));

        if (initialCapacity > 0) {
            this.observable = observable.cacheWithInitialCapacity(initialCapacity);
        }
        else {
            this.observable = observable;
        }
    }

    public Observable<T> toObservable() {
        return observable;
    }

    public void add(Observable<T> observable) {
        sources.add(observable);
    }
    public void remove(Observable<T> observable) {
        sources.remove(observable);
    }
}
