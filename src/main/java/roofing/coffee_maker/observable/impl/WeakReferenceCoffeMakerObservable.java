package roofing.coffee_maker.observable.impl;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import roofing.coffee_maker.CoffeeMaker;
import roofing.coffee_maker.observable.CoffeeMakerObservable;
import roofing.coffee_maker.observable.CoffeeMakerObserver;

public class WeakReferenceCoffeMakerObservable implements CoffeeMakerObservable {

    private final CoffeeMaker observed;
    private final Set<WeakObservableRegistration> observerRegistrations;

    public WeakReferenceCoffeMakerObservable(CoffeeMaker observed) {
        this.observed = observed;
        this.observerRegistrations = new HashSet<>();
    }

    @Override
    public ObservableRegistration register(CoffeeMakerObserver observer) {
        WeakObservableRegistration registration = new WeakObservableRegistration(observer);
        observerRegistrations.add(registration);
        return registration;
    }

    @Override
    public void notifyObservers() {
        observerRegistrations.forEach(reg -> {
            CoffeeMakerObserver weakObserver = reg.observer.get();

            if (weakObserver == null) {
                observerRegistrations.remove(reg);
            } else {
                weakObserver.observe(observed);
            }
        });
    }

    private class WeakObservableRegistration implements ObservableRegistration {

        protected final WeakReference<CoffeeMakerObserver> observer;

        private final UUID uniqueId = UUID.randomUUID();

        protected WeakObservableRegistration(CoffeeMakerObserver observer) {
            this.observer = new WeakReference<>(observer);
        }

        @Override
        public void remove() {
            observerRegistrations.remove(this);
        }

        @Override
        public String toString() {
            return uniqueId.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            WeakObservableRegistration other = (WeakObservableRegistration) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance())) {
                return false;
            }
            if (uniqueId == null) {
                if (other.uniqueId != null) {
                    return false;
                }
            } else if (!uniqueId.equals(other.uniqueId)) {
                return false;
            }
            return true;
        }

        private WeakReferenceCoffeMakerObservable getEnclosingInstance() {
            return WeakReferenceCoffeMakerObservable.this;
        }
    }
}
