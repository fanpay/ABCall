import unittest
from src.commands.ping import ViewPing


class TestPing(unittest.TestCase):

    def test_ping(self):
        result = ViewPing().execute()
        assert result == "pong"
