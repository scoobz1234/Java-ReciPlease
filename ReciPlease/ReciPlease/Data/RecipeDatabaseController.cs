using ReciPlease.Classes;
using SQLite;
using System;
using Xamarin.Forms;

namespace ReciPlease.Data
{
    public class RecipeDatabaseController
    {
        static object locker = new object();

        SQLiteConnection database;

        public RecipeDatabaseController()
        {
            database = DependencyService.Get<ISQLite>().GetConnection();
            database.CreateTable<Recipe>();
        }

        public Recipe GetRecipe()
        {
            lock (locker)
            {
                return database.Table<Recipe>().Count() == 0 ? (Recipe)null : database.Table<Recipe>().First();
            }
        }

        public int SaveRecipe(Recipe r)
        {
            lock (locker)
            {
                if (r.Id != 0)
                {
                    database.Update(r);
                    return r.Id;
                }
                else
                {
                    return database.Insert(r);
                }
            }
        }

        public int DeleteRecipe(int id)
        {
            lock (locker)
            {
                return database.Delete<Recipe>(id);
            }
        }
    }
}
