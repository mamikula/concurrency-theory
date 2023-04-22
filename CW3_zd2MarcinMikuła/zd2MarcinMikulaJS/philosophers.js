// Teoria Współbieżnośi, implementacja problemu 5 filozofów w node.js
// Opis problemu: http://en.wikipedia.org/wiki/Dining_philosophers_problem
//   https://pl.wikipedia.org/wiki/Problem_ucztuj%C4%85cych_filozof%C3%B3w
// 1. Dokończ implementację funkcji podnoszenia widelca (Fork.acquire).
// 2. Zaimplementuj "naiwny" algorytm (każdy filozof podnosi najpierw lewy, potem
//    prawy widelec, itd.).
// 3. Zaimplementuj rozwiązanie asymetryczne: filozofowie z nieparzystym numerem
//    najpierw podnoszą widelec lewy, z parzystym -- prawy. 
// 4. Zaimplementuj rozwiązanie z kelnerem (według polskiej wersji strony)
// 5. Zaimplementuj rozwiążanie z jednoczesnym podnoszeniem widelców:
//    filozof albo podnosi jednocześnie oba widelce, albo żadnego.
// 6. Uruchom eksperymenty dla różnej liczby filozofów i dla każdego wariantu
//    implementacji zmierz średni czas oczekiwania każdego filozofa na dostęp 
//    do widelców. Wyniki przedstaw na wykresach.

var Fork = function () {
    this.state = 0;
    return this;
}

Fork.prototype.acquire = function (cb) {
    // zaimplementuj funkcję acquire, tak by korzystala z algorytmu BEB
    // (http://pl.wikipedia.org/wiki/Binary_Exponential_Backoff), tzn:
    // 1. przed pierwszą próbą podniesienia widelca Filozof odczekuje 1ms
    // 2. gdy próba jest nieudana, zwiększa czas oczekiwania dwukrotnie
    //    i ponawia próbę, itd.
    var fork = this,
        loop = function (waitTime) {
            setTimeout(function () {
                if (fork.state == 0) {
                    fork.state = 1;
                    cb();
                }
                else
                    loop(waitTime * 2);
            }, waitTime);
        };
    loop(1);
}

Fork.prototype.release = function () {
    this.state = 0;
}

var Conductor = function (n) {
    this.state = n;
    return this.state;
}

Conductor.prototype.acquire = function (cb) {
    var conductor = this,
        loop = function (waitTime) {
            setTimeout(function () {
                if (conductor.state > 0) {
                    conductor.state -= 1;
                    cb();
                }
                else
                    loop(waitTime * 2);
            }, waitTime);
        };
    loop(1);
}

Conductor.prototype.release = function () {
    this.state += 1;
}

var Philosopher = function (id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id + 1) % forks.length;
    this.eatenMeals = 0;
    this.waitingTime = 0;
    this.startWaitingTime = -1;
    return this;
}

Philosopher.prototype.startNaive = function (count) {

    this.waitingTime = 0;
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        eatenMeals = this.eatenMeals,
        self = this,

        // zaimplementuj rozwiązanie naiwne
        // każdy filozof powinien 'count' razy wykonywać cykl
        // podnoszenia widelców -- jedzenia -- zwalniania widelców

        naive = function (count) {
            self.startWaitingTime = new Date().getTime();
            if (count > 0) {
                forks[f1].acquire(function () {
                    forks[f2].acquire(function () {
                        self.waitingTime += new Date().getTime() - self.startWaitingTime;
                        setTimeout(function () {
                            count -= 1;
                            forks[f1].release();
                            forks[f2].release();
                            eatenMeals += 1;
                            console.log("Philosopher " + id + " has eaten " + eatenMeals + " meal");
                            naive(count - 1);
                        }, 1)
                    })
                })
            }
            else {
                philosophersEating -= 1;
            }
        }
    naive(count);
}

Philosopher.prototype.startAsym = function (count) {
    this.waitingTime = 0;
    var forks = this.forks,

        f1 = this.id % 2 == 0 ? this.f1 : this.f2,
        f2 = this.id % 2 == 0 ? this.f2 : this.f1,
        id = this.id,
        eatenMeals = this.eatenMeals,
        self = this,


        // zaimplementuj rozwiązanie asymetryczne
        // każdy filozof powinien 'count' razy wykonywać cykl
        // podnoszenia widelców -- jedzenia -- zwalniania widelców

        asym = function (count) {
            self.startWaitingTime = new Date().getTime();
            if (count > 0) {
                forks[f1].acquire(function () {
                    forks[f2].acquire(function () {
                        self.waitingTime += new Date().getTime() - self.startWaitingTime;
                        setTimeout(function () {
                            forks[f1].release();
                            forks[f2].release();
                            eatenMeals += 1;
                            console.log("Philosopher " + id + " has eaten " + eatenMeals + " meal");
                            asym(count - 1);
                        }, 1)
                    })
                })
            }
            else {
                philosophersEating -= 1;
                if (philosophersEating == 0) callNext();
            }
        };
    asym(count);
}

Philosopher.prototype.startConductor = function (count, conductor) {
    this.waitingTime = 0;

    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        eatenMeals = this.eatenMeals,
        self = this,

        // zaimplementuj rozwiązanie z kelnerem
        // każdy filozof powinien 'count' razy wykonywać cykl
        // podnoszenia widelców -- jedzenia -- zwalniania widelców
        loopConductor = function (count) {
            self.startWaitingTime = new Date().getTime();
            if (count > 0) {
                conductor.acquire(function () {
                    forks[f1].acquire(function () {
                        forks[f2].acquire(function () {
                            self.waitingTime += new Date().getTime() - self.startWaitingTime;
                            setTimeout(function () {
                                forks[f1].release();
                                forks[f2].release();
                                eatenMeals += 1;
                                console.log("Philosopher " + id + " has eaten " + eatenMeals + " meal and walk away");
                                conductor.release();
                                loopConductor(count - 1);
                            }, 1)
                        })
                    })
                })
            }
            else {
                philosophersEating -= 1;
                if (philosophersEating == 0) callNext();
            }
        };
    loopConductor(count)
}

function acquireSim(fork1, fork2, cb) {
    var loop = function (waitTime) {
        setTimeout(function () {
            if (fork1.state == 0 && fork2.state == 0) {
                fork1.state = fork2.state = 1;
                cb();
            }
            else
                loop(waitTime * 2);
        }, waitTime);
    };

    loop(1);
}

Philosopher.prototype.startSimult = function (count) {

    // TODO: wersja z jednoczesnym podnoszeniem widelców
    // Algorytm BEB powinien obejmować podnoszenie obu widelców, 
    // a nie każdego z osobna

    this.waitingTime = 0;

    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        eatenMeals = this.eatenMeals,
        self = this,

        loopSimult = function (count) {
            self.startWaitingTime = new Date().getTime();
            if (count > 0) {
                acquireSim(forks[f1], forks[f2], function () {
                    self.waitingTime += new Date().getTime() - self.startWaitingTime;
                    setTimeout(function () {
                        forks[f1].release();
                        forks[f2].release();
                        eatenMeals += 1;
                        console.log("Philosopher " + id + " has eaten " + eatenMeals + " meal");
                        loopSimult(count - 1);
                    }, 1)
                })
            }
            else {
                philosophersEating -= 1;
                if (philosophersEating == 0) callNext();
            }
        };
    loopSimult(count);
}

function printTimes() {
    for (var i = 0; i < N; i++) {
        console.log("Philosopher " + i + " average waiting time " + philosophers[i].waitingTime / meals + " ms\n");
    }
}


var N = 10;
var forks = [];
var philosophers = []
var conductor = new Conductor(N - 1);
var meals = 5;
var philosophersEating = N;

for (var i = 0; i < N; i++) {
    forks.push(new Fork());
}

for (var i = 0; i < N; i++) {
    philosophers.push(new Philosopher(i, forks));
}


for (var i = 0; i < N; i++) {
    philosophers[i].startAsym(meals);
}

philosophersEating = N;
callNext = function () {
    console.log("Asym:\n");
    printTimes();

    for (var i = 0; i < N; i++) {
        philosophers[i].startSimult(meals);
    }

    philosophersEating = N;
    callNext = function () {
        console.log("Simult:\n");
        printTimes();

        for (var i = 0; i < N; i++) {
            philosophers[i].startConductor(meals, conductor);
        }

        philosophersEating = N;
        callNext = function () {
            console.log("Conductor:\n");
            printTimes();
        }
    }
}	
