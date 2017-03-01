# SmartRoad

## Environnement de développement

- Android Studio
- Git

## Installation

- Clonez le repository de l'application avec la commande :
`git clone https://github.com/Paltoquet/SmartRoadv2.git`
- Ouvrez le `build.gradle` du répertoire à l'aide d'Android Studio
- Branchez votre téléphone et lancez la compilation
- Une fois dans l'application pensez à changer l'adresse IP à l'aide du bouton `IP`

## Utilisation

- Lors de votre premier trajet, allez dans le menu `START`, lancez le record et roulez. 
  - Ce record va capturer un point toutes les minutes.
  - Une fois votre trajet terminé, appuyez sur le bouton `STOP` et envoyez le résultat au serveur (une fois dans le même réseau si celui-ci n'est pas sur un cloud).
- Lors de votre deuxième trajet, allez dans le menu proximity, ajouter les checkpoints à l'aide du bouton `ADD PROXIMITY`.
  - Vous pouvez ensuite rouler normalement. Si le serveur est en ligne, vous recevrez des prédictions à chaque checkpoints.
  - Une fois le chemin terminé, envoyez les résultats au serveur à l'aide du bouton `SEND`.
- Vous pouvez consulter vos données de trajet dans le menu `STATS`.
- Vous pouvez demander une prédiction d'heure d'arrivé à l'aide du menu `LEAVING NOW`.
  - Dans cette page, vous avez également deux boutons tests qui sont là pour être utilisés lors de la démonstration.

```
void xorFunction2(Ptr<Int> p, Ptr<Int> q, Ptr<Int> r, Int n)
{
  Int inc = numQPUs() << 4;

  Ptr<Int> a = p + index() + (me() << 4);
  Ptr<Int> b = q + index() + (me() << 4);
  Ptr<Int> c = r + index() + (me() << 4);
  gather(a);gather(b);
  
  Int pOld, qOld, rOld;

  For(Int i = 0, i<n-1, i=i+inc)
    gather(a+inc); gather(b+inc);
    receive(pOld); receive(qOld);
    
    store(pOld^qOld, c);
    
    a = a+inc; b=b+inc; c=c+inc;
  End
  receive(pOld); receive(qOld);
  store(pOld^qOld, c);
}
```
