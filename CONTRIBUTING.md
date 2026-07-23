# Règles de contribution - Projet LISA

## Structure des branches

main : version stable (production)

dev : branche principale de développement

fonctionnalite/* : branches pour développer une fonctionnalité

---

## Workflow de travail

1. Se placer sur la branche dev

git checkout dev
git pull origin dev

2. Créer une branche fonctionnalité

git checkout -b fonctionnalite/nom-fonctionnalite

3. Développer la fonctionnalité

4. Push sur GitHub

git push origin fonctionnalite/nom-fonctionnalite

5. Créer une Pull Request vers dev

6. Après validation, la branche est fusionnée dans dev.

---

## Règles importantes

- Ne jamais travailler directement sur main
- Toujours créer une branche fonctionnalite
- Toute modification passe par Pull Request
- Les commits doivent être clairs
