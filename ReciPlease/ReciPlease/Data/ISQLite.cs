﻿using SQLite;
using System;
using System.Collections.Generic;
using System.Text;

namespace ReciPlease.Data
{
    public interface ISQLite
    {
        SQLiteConnection GetConnection();
    }
}
