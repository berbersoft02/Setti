# 🌊 SettiFlow - Configuration pour Mamie (94 ans)

## ✅ Modifications terminées

Votre application a été transformée en un **système d'alarme vocal automatique** parfait pour votre grand-mère qui ne peut pas lire ou utiliser le téléphone.

---

## 🎯 Comment ça fonctionne maintenant

### 1. **Alarme Plein Écran avec Voix** 🔊
- Toutes les **2 heures** (personnalisable), le téléphone :
  - **S'allume automatiquement** même s'il est verrouillé
  - **Joue une alarme forte** (son + vibration)
  - **Parle en français** : *"C'est l'heure de boire de l'eau ! Buvez un verre d'eau maintenant !"*
  - **Répète le message 3 fois** pour être sûr qu'elle l'entende
  - **Affiche une énorme icône de goutte d'eau** en bleu qui pulse

### 2. **Arrêt Automatique** ⏰
- L'alarme s'arrête automatiquement après **3 minutes**
- Pas besoin d'interaction si elle ne peut pas toucher le téléphone
- Elle peut juste écouter et boire son eau

### 3. **Heures de Silence** 🌙
- **Aucune alarme entre 22h (10 PM) et 7h (7 AM)**
- Elle peut dormir tranquillement sans être dérangée

### 4. **Volume Maximum** 📢
- Le système met automatiquement le volume au maximum pendant l'alarme
- Impossible de rater l'alarme même si le téléphone est dans une autre pièce

---

## 📱 Installation

### Étape 1 : Installer l'APK
```bash
# Générer l'APK
./gradlew assembleDebug

# L'APK sera dans :
# app/build/outputs/apk/debug/app-debug.apk

# Transférez-le sur le téléphone de votre grand-mère et installez-le
```

### Étape 2 : Configuration Initiale
1. **Lancez l'application** une première fois
2. Sur l'écran d'onboarding :
   - Entrez son prénom (par défaut : "Mamie")
   - Définissez l'objectif quotidien (par défaut : 8 verres = 2L)
   - **Activez les rappels** (switch ON)
   - Choisissez la fréquence : **"Toutes les 2h"** (recommandé)
3. Appuyez sur **"Enregistrer et Commencer"**

### Étape 3 : Permissions Importantes
Autorisez ces permissions quand Android les demande :
- ✅ **Notifications** - pour l'alarme
- ✅ **Afficher par-dessus les autres apps** - pour le plein écran
- ✅ **Alarmes et rappels** - pour programmer les rappels
- ✅ **Ne pas déranger** - pour contourner le mode silencieux

---

## ⚙️ Personnalisation

### Changer la fréquence des rappels
Dans le code, modifiez `reminderIntervalMinutes` :
- **60** = toutes les heures
- **120** = toutes les 2 heures (par défaut)
- **180** = toutes les 3 heures

**Fichier** : `app/src/main/java/com/example/data/UserSettings.kt`
```kotlin
val reminderIntervalMinutes: Int = 120, // Changez cette valeur
```

### Changer les heures de silence
**Fichier** : `app/src/main/java/com/example/data/UserSettings.kt`
```kotlin
val quietHoursStart: Int = 22, // 22h (10 PM) - début du silence
val quietHoursEnd: Int = 7     // 7h (7 AM) - fin du silence
```

### Changer le message vocal
**Fichier** : `app/src/main/java/com/example/AlarmActivity.kt` ligne 99
```kotlin
val message = "C'est l'heure de boire de l'eau ! Buvez un verre d'eau maintenant !"
```
Vous pouvez le changer en :
- *"Mamie, c'est l'heure de boire ton verre d'eau !"*
- *"N'oublie pas de boire de l'eau !"*
- Etc.

### Changer le nombre de répétitions
**Fichier** : `app/src/main/java/com/example/AlarmActivity.kt` ligne 104
```kotlin
repeat(2) {  // Changez 2 en 3 ou 4 pour plus de répétitions
```

---

## 🔧 Recommandations pour le téléphone de Mamie

### 1. **Désactiver la mise en veille du téléphone**
- Paramètres → Affichage → Veille → **Jamais** ou **30 minutes**

### 2. **Désactiver le mode économie d'énergie**
- Paramètres → Batterie → Économie d'énergie → **Désactivé**
- Désactiver l'optimisation de batterie pour l'application

### 3. **Volume par défaut au maximum**
- Mettez tous les volumes (sonnerie, média, alarme) au maximum

### 4. **Mode Ne pas déranger → Exceptions**
- Ajoutez l'application dans les exceptions du mode "Ne pas déranger"

### 5. **Gardez le téléphone branché**
- Branchez le téléphone sur secteur en permanence
- Placez-le dans un endroit visible et accessible

### 6. **Position du téléphone**
- Mettez-le dans la pièce où elle passe le plus de temps
- Face visible pour qu'elle voie l'écran s'allumer

---

## 🎨 Interface Simplifiée

L'interface principale reste fonctionnelle mais vous pouvez :
1. **Désactiver les autres onglets** si elle n'en a pas besoin
2. **Masquer le clavier** pour éviter toute interaction accidentelle
3. L'application fonctionne **complètement en automatique** - aucune interaction nécessaire

---

## 📊 Suivi à distance

Si vous voulez suivre sa consommation d'eau :
1. L'application enregistre automatiquement chaque verre bu (si quelqu'un appuie sur "J'ai bu")
2. Vous pouvez consulter l'historique dans l'onglet "Historique"
3. Future amélioration possible : synchronisation cloud pour consulter à distance

---

## 🆘 Dépannage

### L'alarme ne sonne pas ?
1. Vérifiez que les rappels sont activés dans les paramètres de l'app
2. Vérifiez les permissions (notifications, alarmes)
3. Désactivez le mode économie d'énergie
4. Vérifiez que l'heure actuelle n'est pas dans les heures de silence (22h-7h)

### L'alarme est trop forte/faible ?
- Le système utilise le volume ALARME du téléphone
- Réglez le volume d'alarme dans les paramètres Android (pas le volume média)

### Le message vocal ne se joue pas ?
1. Vérifiez que le **Text-to-Speech** (synthèse vocale) est activé dans Android
2. Paramètres → Accessibilité → Sortie de synthèse vocale → Moteur Google

### L'alarme ne s'affiche pas en plein écran ?
1. Paramètres → Applications → SettiFlow → Permissions spéciales
2. Activez **"Afficher par-dessus les autres apps"**

---

## 📞 Contact & Soutien

Cette application a été spécialement adaptée pour votre grand-mère de 94 ans.
Elle n'a **rien à faire** - le téléphone lui rappellera automatiquement de boire toutes les 2 heures avec une alarme vocale forte et claire.

**Que Dieu bénisse Mamie et qu'elle reste bien hydratée ! 💙💧**

---

## 📝 Checklist Installation

- [ ] APK installé sur le téléphone
- [ ] Application lancée et onboarding complété
- [ ] Toutes les permissions accordées (notifications, alarmes, plein écran)
- [ ] Rappels activés (switch ON)
- [ ] Fréquence réglée sur 2 heures
- [ ] Volume d'alarme au maximum
- [ ] Économie d'énergie désactivée
- [ ] Téléphone branché sur secteur
- [ ] Téléphone placé dans un endroit visible
- [ ] Text-to-Speech (synthèse vocale) activé
- [ ] Test de la première alarme effectué ✅

---

**Version de l'application** : SettiFlow v1.0 - Édition Spéciale Mamie
**Date** : 17 août 2026
