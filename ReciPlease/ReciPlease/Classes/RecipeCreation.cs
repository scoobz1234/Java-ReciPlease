using ReciPlease.HelperClasses;
using System;
using System.Collections.Generic;
using System.Text;

namespace ReciPlease.Classes
{
    public class RecipeCreation
    {
        public List<Ingredient> ingredients = new List<Ingredient>();

        void CreateIngredient(string _amount, Enums.MeasurementTypes _measurement, string _ingredientName)
        {
            Ingredient i = new Ingredient(_amount, _measurement, _ingredientName);
            ingredients.Add(i);

        }

    }
}
