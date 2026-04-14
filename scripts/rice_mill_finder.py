#!/usr/bin/env python3
"""
rice_mill_finder.py
-------------------
Searches Google Maps for rice mills in Kaithal and Karnal districts,
cross-checks each result against an existing spreadsheet, and appends
any new mills (not already in the sheet) to the spreadsheet.

Usage:
    python rice_mill_finder.py --file <path_to_spreadsheet> \
                               --name-col <column_name> \
                               [--sheet <sheet_name>] \
                               [--threshold <0-100>] \
                               [--log <path_to_log_csv>]

Environment Variables:
    GOOGLE_MAPS_API_KEY  – Your Google Maps API key with Places API enabled.

Examples:
    python rice_mill_finder.py --file rice_mills.xlsx --name-col "Mill Name"
    python rice_mill_finder.py --file data.csv --name-col Name --threshold 85 \
                               --log new_mills.csv
"""

import argparse
import csv
import os
import sys
import time

import googlemaps
import pandas as pd
from rapidfuzz import fuzz, process

# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------

DISTRICTS = ["Kaithal", "Karnal"]
QUERY_TEMPLATE = "rice mill in {district}, Haryana, India"
# Google Places Text Search returns up to 20 results per page; 3 pages = 60 max
MAX_PAGES = 3
# Seconds to wait before requesting the next page (Google requires a short delay
# for the next_page_token to become valid). Can be overridden via --page-delay.
DEFAULT_PAGE_DELAY = 2

SPREADSHEET_COLUMNS = [
    "Name",
    "Address",
    "Phone",
    "District",
    "Latitude",
    "Longitude",
    "Place ID",
    "Rating",
]

# ---------------------------------------------------------------------------
# Google Maps helpers
# ---------------------------------------------------------------------------


def fetch_mills_for_district(
    gmaps_client: googlemaps.Client, district: str, page_delay: int = DEFAULT_PAGE_DELAY
) -> list[dict]:
    """Return a list of rice mill details for a given district."""
    query = QUERY_TEMPLATE.format(district=district)
    results = []
    response = gmaps_client.places(query=query)

    for _ in range(MAX_PAGES):
        for place in response.get("results", []):
            details = _fetch_place_details(gmaps_client, place["place_id"])
            results.append(
                {
                    "Name": place.get("name", ""),
                    "Address": place.get("formatted_address", ""),
                    "Phone": details.get("formatted_phone_number", ""),
                    "District": district,
                    "Latitude": place.get("geometry", {}).get("location", {}).get("lat", ""),
                    "Longitude": place.get("geometry", {}).get("location", {}).get("lng", ""),
                    "Place ID": place.get("place_id", ""),
                    "Rating": place.get("rating", ""),
                }
            )

        next_page_token = response.get("next_page_token")
        if not next_page_token:
            break

        # Google requires a short delay before the next-page token becomes valid
        time.sleep(page_delay)
        response = gmaps_client.places(query=query, page_token=next_page_token)

    return results


def _fetch_place_details(gmaps_client: googlemaps.Client, place_id: str) -> dict:
    """Fetch detailed fields (e.g. phone number) for a single place."""
    try:
        detail_response = gmaps_client.place(
            place_id=place_id,
            fields=["formatted_phone_number"],
        )
        return detail_response.get("result", {})
    except googlemaps.exceptions.ApiError as exc:
        print(f"  Warning: Places API error for place_id={place_id}: {exc}", file=sys.stderr)
        return {}
    except Exception as exc:
        print(f"  Warning: Unexpected error fetching details for place_id={place_id}: {exc}", file=sys.stderr)
        return {}


# ---------------------------------------------------------------------------
# Spreadsheet helpers
# ---------------------------------------------------------------------------


def load_spreadsheet(file_path: str, sheet_name: str | None, name_col: str) -> pd.DataFrame:
    """Load the spreadsheet into a DataFrame.

    Supports .xlsx / .xls (via openpyxl / xlrd) and .csv files.
    Raises ValueError if the name column is not found.
    """
    ext = os.path.splitext(file_path)[1].lower()
    if ext in (".xlsx", ".xls"):
        kwargs = {"sheet_name": sheet_name} if sheet_name else {}
        df = pd.read_excel(file_path, **kwargs)
    elif ext == ".csv":
        df = pd.read_csv(file_path)
    else:
        raise ValueError(f"Unsupported file format: '{ext}'. Use .xlsx, .xls, or .csv.")

    if name_col not in df.columns:
        raise ValueError(
            f"Column '{name_col}' not found in spreadsheet. "
            f"Available columns: {list(df.columns)}"
        )

    return df


def save_spreadsheet(df: pd.DataFrame, file_path: str, sheet_name: str | None) -> None:
    """Save the DataFrame back to the original file."""
    ext = os.path.splitext(file_path)[1].lower()
    if ext in (".xlsx", ".xls"):
        kwargs = {"sheet_name": sheet_name or "Sheet1", "index": False}
        df.to_excel(file_path, **kwargs)
    elif ext == ".csv":
        df.to_csv(file_path, index=False)


# ---------------------------------------------------------------------------
# Cross-check logic
# ---------------------------------------------------------------------------


def is_existing_mill(
    name: str,
    existing_names: list[str],
    threshold: int,
) -> bool:
    """Return True if *name* fuzzy-matches any entry in *existing_names*."""
    if not existing_names:
        return False
    match = process.extractOne(name, existing_names, scorer=fuzz.token_sort_ratio)
    return match is not None and match[1] >= threshold


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Find rice mills in Kaithal & Karnal via Google Maps and update a spreadsheet."
    )
    parser.add_argument(
        "--file",
        required=True,
        metavar="PATH",
        help="Path to the existing spreadsheet (.xlsx or .csv).",
    )
    parser.add_argument(
        "--name-col",
        required=True,
        metavar="COLUMN",
        help="Name of the column in the spreadsheet that contains mill names.",
    )
    parser.add_argument(
        "--sheet",
        default=None,
        metavar="SHEET",
        help="Sheet name to read/write (Excel only). Defaults to the first sheet.",
    )
    parser.add_argument(
        "--threshold",
        type=int,
        default=80,
        metavar="0-100",
        help="Fuzzy-match similarity threshold (0–100). Default: 80.",
    )
    parser.add_argument(
        "--page-delay",
        type=int,
        default=DEFAULT_PAGE_DELAY,
        metavar="SECONDS",
        help=(
            f"Seconds to wait between paginated Google Maps requests. "
            f"Default: {DEFAULT_PAGE_DELAY}."
        ),
    )
    parser.add_argument(
        "--log",
        default=None,
        metavar="PATH",
        help="Optional path to write a CSV log of newly added mills.",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()

    # --- API key ---
    api_key = os.environ.get("GOOGLE_MAPS_API_KEY")
    if not api_key:
        print(
            "ERROR: The environment variable GOOGLE_MAPS_API_KEY is not set.\n"
            "Export it before running this script:\n"
            "  export GOOGLE_MAPS_API_KEY='your_key_here'",
            file=sys.stderr,
        )
        sys.exit(1)

    gmaps = googlemaps.Client(key=api_key)

    # --- Load spreadsheet ---
    print(f"Loading spreadsheet: {args.file}")
    try:
        df = load_spreadsheet(args.file, args.sheet, args.name_col)
    except (ValueError, FileNotFoundError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        sys.exit(1)

    existing_names = df[args.name_col].dropna().astype(str).tolist()
    print(f"  {len(existing_names)} existing mill(s) loaded from spreadsheet.")

    # --- Search Google Maps ---
    all_found: list[dict] = []
    for district in DISTRICTS:
        print(f"\nSearching Google Maps for rice mills in {district}…")
        mills = fetch_mills_for_district(gmaps, district, page_delay=args.page_delay)
        print(f"  Found {len(mills)} result(s) for {district}.")
        all_found.extend(mills)

    total_found = len(all_found)
    print(f"\nTotal mills found across all districts: {total_found}")

    # --- Cross-check & filter ---
    new_mills: list[dict] = []
    skipped = 0

    for mill in all_found:
        if is_existing_mill(mill["Name"], existing_names, args.threshold):
            skipped += 1
        else:
            new_mills.append(mill)

    print(f"\nCross-check complete:")
    print(f"  Already in spreadsheet (skipped): {skipped}")
    print(f"  New mills to add:                 {len(new_mills)}")

    if not new_mills:
        print("\nNo new mills to add. Spreadsheet is up to date.")
        return

    # --- Append new mills to spreadsheet ---
    new_df = pd.DataFrame(new_mills, columns=SPREADSHEET_COLUMNS)

    # Align columns: add any missing SPREADSHEET_COLUMNS to the existing df
    for col in SPREADSHEET_COLUMNS:
        if col not in df.columns:
            df[col] = ""

    updated_df = pd.concat([df, new_df], ignore_index=True)
    save_spreadsheet(updated_df, args.file, args.sheet)
    print(f"\nSpreadsheet updated: {args.file}")

    # --- Optional CSV log ---
    if args.log:
        with open(args.log, "w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=SPREADSHEET_COLUMNS)
            writer.writeheader()
            writer.writerows(new_mills)
        print(f"New-mills log written: {args.log}")

    # --- Final summary ---
    print(
        f"\n=== Summary ===\n"
        f"  Districts searched : {', '.join(DISTRICTS)}\n"
        f"  Total mills found  : {total_found}\n"
        f"  Already existed    : {skipped}\n"
        f"  New mills added    : {len(new_mills)}\n"
    )


if __name__ == "__main__":
    main()
