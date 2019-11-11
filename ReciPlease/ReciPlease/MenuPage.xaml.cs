using System.Collections.Generic;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using SQLite;
using ReciPlease.Classes;

namespace ReciPlease
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class MenuPage : ContentPage
    {
        List<Recipe> rec = new List<Recipe>();
        
        public MenuPage()
        {
            InitializeComponent();
        }

        //everytime the user looks at the page this is called...
        protected override void OnAppearing()
        {
            base.OnAppearing();

            using (SQLiteConnection conn = new SQLiteConnection(App.FilePath)) 
            {
                //create table(if it doesnt exist)...
                conn.CreateTable<Recipe>();
                //create a list from the table using system.linq
                //var recipes = conn.Table<Recipe>().ToList();
                List<Recipe> recip = conn.Table<Recipe>().ToList();

                List<string> recipeNames = new List<string>();


                foreach (Recipe r in recip) 
                {
                    recipeNames.Add(r.ToString());
                    rec.Add(r);                    
                }
                
                RecipeList.ItemsSource = rec;

            }
        }

        //when user taps the listview item
        private async void RecipeList_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            if(e.Item != null)
            {
                var selectedItem = e.Item as Recipe;
                await Navigation.PushAsync(new RecipePage(selectedItem));
            }
            ((ListView)sender).SelectedItem = null;
        }
    }
}