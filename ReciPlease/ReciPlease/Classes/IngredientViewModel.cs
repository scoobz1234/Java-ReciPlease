using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace ReciPlease.Classes
{
    public class IngredientViewModel
    {
        public ObservableCollection<Ingredient> Ingredients { get; set; }

        public IngredientViewModel() 
        {
            Ingredients = new ObservableCollection<Ingredient>
            {
            };
        }

    }
}
