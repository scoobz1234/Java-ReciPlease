using System;
using System.Collections.Generic;
using ReciPlease.Classes;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using SQLite;
using System.Diagnostics;

namespace ReciPlease
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class RecipePage : ContentPage
    {
        Recipe selectedRecipe = null;
        bool itemSelected = false;
        Ingredient selectedIngredient = null;
        object previouslySelectedItem = null;

        public RecipePage(Recipe rec)
        {
            selectedRecipe = rec;
            InitializeComponent();
            ConnectAndRetrieve(rec);

        }

        private void ConnectAndRetrieve(Recipe r) 
        {
            lblRecipeName.Text = r.recipeName;
            List<Ingredient> ing = new List<Ingredient>();

            using (SQLiteConnection conn = new SQLiteConnection(App.FilePath))
            {
                conn.CreateTable<Ingredient>();
                var ingredients = conn.Table<Ingredient>().ToList();

                foreach (Ingredient i in conn.Table<Ingredient>())
                {
                    if (i.RecipeID == r.Id)
                    {
                        ing.Add(i);
                        IngredientsList.ItemsSource = null;
                        IngredientsList.ItemsSource = ing;
                    }
                }
            }
        }


        //when user taps the listview item
        private void IngredientList_ItemTapped(object sender, ItemTappedEventArgs e)
        {

            if (itemSelected)
            {
                if (((ListView)sender).SelectedItem != previouslySelectedItem) 
                {
                    itemSelected = true;
                    var temp = ((ListView)sender).SelectedItem;
                    selectedIngredient = temp as Ingredient;
                }
                else 
                {
                    itemSelected = false;
                    ((ListView)sender).SelectedItem = null;
                    selectedIngredient = null;
                }
            }
            else 
            {
                itemSelected = true;
                var temp = ((ListView)sender).SelectedItem;
                selectedIngredient = temp as Ingredient;
            }

            previouslySelectedItem = ((ListView)sender).SelectedItem;
        }

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
                    ((ListView)sender).SelectedItem = null;
                }
                ConnectAndRetrieve(selectedRecipe); 
            }
        }
    }
}