 /*
  * @Copyright© Stephen R. Ouellette
  * @Author Stephen R. Ouellette <Stephen.R.Ouellette@Gmail.com>
  * 
  * DISCLAIMER
  * 
  * Do not edit or add to this file if you wish to upgrade to newer
  * versions in the future. If you wish to customize ReciPlease for
  * your own needs please do so at your own risk.
  * 
  */

using System;
using System.Collections.Generic;
using ReciPlease.Classes;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using SQLite;

namespace ReciPlease
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class RecipePage : ContentPage
    {
        bool itemSelected = false;
        Recipe selectedRecipe = null;
        Ingredient selectedIngredient = null;
        object previouslySelectedItem = null;

        public RecipePage(Recipe rec)
        {
            selectedRecipe = rec;
            InitializeComponent();
            ConnectAndRetrieve(rec);
        }

        //Connect to the database, and retrieve recipe data
        private void ConnectAndRetrieve(Recipe r) 
        {
            lblRecipeName.Text = r.recipeName; //sets the title of the page to recipe name...
            List<Ingredient> ingredients = new List<Ingredient>();

            using (SQLiteConnection conn = new SQLiteConnection(App.FilePath))
            {
                foreach (Ingredient i in conn.Table<Ingredient>())
                {
                    if (i.RecipeID == r.Id)
                    {
                        ingredients.Add(i);
                        IngredientsList.ItemsSource = null; //set the list to null so we can update it...
                        IngredientsList.ItemsSource = ingredients; //set the list source to the list of ingredients...
                    }
                }
            }
        }

        //when user taps the listview item
        private void IngredientList_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            if (itemSelected) //check if the item is already selected or being selected for the first time...
            {
                if (((ListView)sender).SelectedItem != previouslySelectedItem) 
                {
                    itemSelected = true;
                    var temp = ((ListView)sender).SelectedItem; //cast selected item as object...
                    selectedIngredient = temp as Ingredient; //set selected ingredient to the object as an ingredient...
                }
                else 
                {
                    itemSelected = false; //else we are de-selecting the item
                    ((ListView)sender).SelectedItem = null;
                    selectedIngredient = null;
                }
            }
            else //else we are selecting it for the first time.
            {
                itemSelected = true;
                var temp = ((ListView)sender).SelectedItem;
                selectedIngredient = temp as Ingredient;
            }

            previouslySelectedItem = ((ListView)sender).SelectedItem; //set the previous selected item to the current selected item...
        }

        //when the user taps the edit button
        private async void btnEdit_Clicked(object sender, EventArgs e)
        {
            if (itemSelected) 
            {
                bool choice = await DisplayAlert("Edit Ingredient","Are you sure you want to edit this Ingredient?", "Yes", "No");

                if (choice) 
                {
                    await Navigation.PushAsync(new IngredientPage(selectedIngredient, selectedRecipe));
                }
            }
        }

        //when the user taps the add button
        private async void btnAdd_Clicked(object sender, EventArgs e) 
        {
            bool choice = await DisplayAlert("Add Ingredient", "Do you want to add a new Ingredient", "Yes", "No");
            if (choice) 
            {
                Ingredient newIngredient = new Ingredient();
                using (SQLiteConnection conn = new SQLiteConnection(App.FilePath)) 
                {
                    newIngredient.RecipeID = selectedRecipe.Id;
                    conn.Insert(newIngredient);
                }
                await Navigation.PushAsync(new IngredientPage(newIngredient, selectedRecipe));
            }
        }

        //when the user taps the delete button
        private async void btnDelete_Clicked(object sender, EventArgs e)
        {
            if (itemSelected)
            {
                bool choice = await DisplayAlert("Delete Ingredient", "Do you want to Delete this ingredient", "Yes", "No");
                if (choice)
                {
                    using (SQLiteConnection conn = new SQLiteConnection(App.FilePath))
                    {
                        conn.Delete(selectedIngredient);
                        selectedIngredient = null;
                    }
                    previouslySelectedItem = null;
                    itemSelected = false;
                }
            }
            ConnectAndRetrieve(selectedRecipe);
        }

        private async void btnShare_Clicked(object sender, EventArgs e)
        {
            if (itemSelected)
            {
                bool choice = await DisplayAlert("Share Recipe", "Would you like to share this recipe?", "Yes", "No");

                if (choice)
                {
                    await Navigation.PushAsync(new SharePage(selectedRecipe));
                }
            }
        }
    }
}