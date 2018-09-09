"""import stripe
stripe.api_key = "sk_test_cKMWwC726DfCZHoIonMsPba0"

x = stripe.Charge.create(
    amount=2000,
    currency="usd",
    source="tok_mastercard",
    metadata={'order_id': '6735'}
)

if x:
    print("OK-200")"""
